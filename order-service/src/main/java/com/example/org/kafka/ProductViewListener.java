package com.example.org.kafka;

import com.example.events.ProductChangedEvent;
import com.example.org.service.ProductViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductViewListener {
    private static final Logger log = LoggerFactory.getLogger(ProductViewListener.class);
    @Autowired
    private ProductViewService productViewService;

    @KafkaListener(topics="product.changed", groupId = "order-service")
    public void listen(ProductChangedEvent event) {
        log.debug("Received product changed message id={}, action={}", event.productId(), event.action());
        productViewService.apply(event);
    }
}
