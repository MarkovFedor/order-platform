package org.example.kafka;

import org.example.events.OrderCreatedEvent;
import org.example.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);
    @Autowired
    private StockService stockService;

    @KafkaListener(topics="order.created", groupId = "order-service")
    public void listen(OrderCreatedEvent event) {
        log.info("Order id={} created by event={}  ", event.productId(), event.quanity());
        stockService.reserve(event);
    }
}