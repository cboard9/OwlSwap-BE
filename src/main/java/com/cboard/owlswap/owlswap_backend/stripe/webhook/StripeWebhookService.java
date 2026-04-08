package com.cboard.owlswap.owlswap_backend.stripe.webhook;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;
import com.cboard.owlswap.owlswap_backend.dao.OrderDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.model.orders.ListingStatus;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.model.orders.OrderStatus;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeWebhookService {

    private final OrderDao orderDao;
    private final ItemDao itemDao;

    public StripeWebhookService(OrderDao orderDao,
                                ItemDao itemDao) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
    }

    @Transactional
    public void handleEvent(Event event) {
        String eventType = event.getType();

        switch (eventType) {
            case "checkout.session.completed" -> handleCheckoutSessionCompleted(event);
            case "checkout.session.expired" -> handleCheckoutSessionExpired(event);
            default -> {
                // Ignore unsupported events for now
            }
        }
    }

    @Transactional
    public void handleCheckoutSessionCompleted(Event event) {
        /*if (event.getDataObjectDeserializer().getObject().isEmpty()) {
            throw new IllegalStateException("Stripe event payload could not be deserialized.");
        }

        Session session = (Session) event.getDataObjectDeserializer().getObject().get();*/

        Session session;

        var deserializer = event.getDataObjectDeserializer();

        if (deserializer.getObject().isPresent()) {
            session = (Session) deserializer.getObject().get();
        } else {
            try {
                session = (Session) deserializer.deserializeUnsafe();
            }
            catch(EventDataObjectDeserializationException ex)
            {
                throw new BadRequestException("STRIPE DESERIALIZATION FAILED");
            }
        }

        Order order = orderDao.findByCheckoutSessionId(session.getId())
                .orElseGet(() -> {
                    String clientRef = session.getClientReferenceId();
                    if (clientRef == null || clientRef.isBlank()) {
                        throw new NotFoundException("Order not found for checkout session: " + session.getId());
                    }

                    Integer orderId = Integer.valueOf(clientRef);
                    return orderDao.findById(orderId)
                            .orElseThrow(() -> new NotFoundException(
                                    "Order not found for client_reference_id: " + clientRef
                            ));
                });

        // Idempotency guard:
        // if webhook is retried, don't re-process an already-paid order
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.FULFILLED || order.getStatus() == OrderStatus.REFUNDED) {
            return;
        }

        // Optional sanity check: only allow paid completion for pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in a payable state.");
        }

        // Store Stripe info from the session
        order.setLatestPaymentStatus(session.getPaymentStatus());

        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            throw new IllegalStateException("Checkout session completed but payment status is not paid.");
        }

        if (session.getPaymentIntent() != null) {
            order.setPaymentIntentId(session.getPaymentIntent());
        }

        // Mark order paid
        order.setStatus(OrderStatus.PAID);
        orderDao.save(order);

        // Lock item row before changing listing state
        Item item = itemDao.findByIdForUpdate(order.getItem().getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));

        item.setListingStatus(ListingStatus.SOLD);
        item.setReservedByOrder(null);
        item.setReservedUntil(null);
        item.setAvailable(false); // legacy sync for now

        itemDao.save(item);
    }

    @Transactional
    public void handleCheckoutSessionExpired(Event event) {
        var deserializer = event.getDataObjectDeserializer();

        Session session;
        if (deserializer.getObject().isPresent()) {
            session = (Session) deserializer.getObject().get();
        } else {
            try {
                session = (Session) deserializer.deserializeUnsafe();
            }
            catch(EventDataObjectDeserializationException ex)
            {
                throw new BadRequestException("STRIPE DESERIALIZATION FAILED");
            }
        }

        Order order = orderDao.findByCheckoutSessionId(session.getId())
                .orElseGet(() -> {
                    String clientRef = session.getClientReferenceId();
                    if (clientRef == null || clientRef.isBlank()) {
                        throw new NotFoundException("Order not found for expired checkout session: " + session.getId());
                    }

                    Integer orderId = Integer.valueOf(clientRef);
                    return orderDao.findById(orderId)
                            .orElseThrow(() -> new NotFoundException(
                                    "Order not found for client_reference_id: " + clientRef
                            ));
                });

        // Idempotency / already resolved
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.FULFILLED
                || order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            return;
        }

        // Only pending orders should be expired this way
        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        order.setLatestPaymentStatus(session.getPaymentStatus());
        order.setStatus(OrderStatus.EXPIRED);

        // Clear old checkout session linkage so a future retry can generate a fresh session
        order.setCheckoutSessionId(null);

        orderDao.save(order);

        Item item = itemDao.findByIdForUpdate(order.getItem().getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));

        // Only release inventory if this order is still the reserver
        if (item.getReservedByOrder() != null
                && item.getReservedByOrder().getOrderId().equals(order.getOrderId())) {

            item.setListingStatus(ListingStatus.AVAILABLE);
            item.setReservedByOrder(null);
            item.setReservedUntil(null);
            item.setAvailable(true); // legacy sync
            itemDao.save(item);
        }
    }

    @Transactional
    public void expireCheckoutSessionIfOpenInternal(Order order) throws StripeException {
        if (order.getCheckoutSessionId() == null || order.getCheckoutSessionId().isBlank()) {
            return;
        }

        Session session = Session.retrieve(order.getCheckoutSessionId());

        if ("open".equalsIgnoreCase(session.getStatus())) {
            Session expired = session.expire();
            order.setLatestPaymentStatus(expired.getPaymentStatus());
            orderDao.save(order);
        }
    }
}