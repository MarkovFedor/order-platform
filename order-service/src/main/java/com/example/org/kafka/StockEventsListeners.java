package com.example.org.kafka;

import com.example.org.events.StockReservationFailedEvent;
import com.example.org.events.StockReservedEvent;
import com.example.org.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockEventsListeners {
    private static final Logger log = LoggerFactory.getLogger(StockEventsListeners.class);

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics="stock.reserved", groupId = "order-service")
    public void stockReservedListener(StockReservedEvent event) {
        log.info("Event with order_id={} received", event.orderId());
        orderService.markOrderReserved(event);
    }

    @KafkaListener(topics = "stock.failed", groupId = "order-service")
    public void stockReserveFailedListener(StockReservationFailedEvent event) {
        orderService.markOrderFailedToReserve(event);
    }
}
