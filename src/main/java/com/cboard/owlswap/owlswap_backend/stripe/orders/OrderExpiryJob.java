package com.cboard.owlswap.owlswap_backend.stripe.orders;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;
import com.cboard.owlswap.owlswap_backend.dao.OrderDao;

import com.cboard.owlswap.owlswap_backend.stripe.checkout.StripeCheckoutService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderExpiryJob {

    private final OrderDao orderDao;
    private final ItemDao itemDao;
    private final StripeCheckoutService stripeCheckoutService;

    public OrderExpiryJob(OrderDao orderDao, ItemDao itemDao, StripeCheckoutService stripeCheckoutService) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
        this.stripeCheckoutService = stripeCheckoutService;
    }

    @Scheduled(fixedRate = 60_000) // every minute
    @Transactional
    public void expirePendingOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> expired = orderDao.findByStatusAndReservedUntilBefore(OrderStatus.PENDING, now);

        for (Order order : expired) {
            stripeCheckoutService.expireCheckoutSessionIfOpen(order.getOrderId());
        }
    }
}
