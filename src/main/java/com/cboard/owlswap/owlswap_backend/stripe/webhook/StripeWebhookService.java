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
            /*// Fallback: manually deserialize raw JSON
            session = (Session) com.stripe.model.EventDataObjectDeserializer.deserializeUnsafe(
                    event.getData().getObject().toJson(),
                    Session.class
            );*/
            try {
                session = (Session) deserializer.deserializeUnsafe();
            }
            catch(EventDataObjectDeserializationException ex)
            {
                throw new BadRequestException("STRIPE DESERIALIZATION FAILED");
            }
        }

        //String checkoutSessionId = session.getId();

        /*Order order = orderDao.findByCheckoutSessionId(checkoutSessionId)
                .orElseThrow(() -> new NotFoundException(
                        "Order not found for checkout session: " + checkoutSessionId
                ));*/

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
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.FULFILLED) {
            return;
        }

        // Optional sanity check: only allow paid completion for pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in a payable state.");
        }

        // Store Stripe info from the session
        order.setLatestPaymentStatus(session.getPaymentStatus());

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
}