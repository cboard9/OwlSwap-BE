package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;
import com.cboard.owlswap.owlswap_backend.dao.OrderDao;
import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.model.orders.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderExpiryJob {

    private final OrderDao orderDao;
    private final ItemDao itemDao;

    public OrderExpiryJob(OrderDao orderDao, ItemDao itemDao) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
    }

    @Scheduled(fixedRate = 60_000) // every minute
    @Transactional
    public void expirePendingOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> expired = orderDao.findByStatusAndReservedUntilBefore(OrderStatus.PENDING, now);

        for (Order order : expired) {
            order.setStatus(OrderStatus.EXPIRED);
            orderDao.save(order);

            Item item = itemDao.findByIdForUpdate(order.getItem().getItemId()).orElse(null);
            if (item != null
                    && item.getReservedByOrder() != null
                    && item.getReservedByOrder().getOrderId().equals(order.getOrderId())) {

                item.setListingStatus(ListingStatus.AVAILABLE);
                item.setReservedByOrder(null);
                item.setReservedUntil(null);
                item.setAvailable(true); // legacy
                itemDao.save(item);
            }
        }
    }
}
