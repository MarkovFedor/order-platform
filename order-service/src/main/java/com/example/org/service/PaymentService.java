package com.example.org.service;

import com.example.events.PaymentCancelledEvent;
import com.example.events.PaymentCompletedEvent;
import com.example.events.PaymentRequestedEvent;
import com.example.org.entity.Order;
import com.example.org.entity.OrderStatus;
import com.example.org.entity.OutboxEvent;
import com.example.org.entity.ProcessedEvent;
import com.example.org.repository.OrderRepository;
import com.example.org.repository.OutBoxRepository;
import com.example.org.repository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutBoxRepository outBoxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void acceptPayment(PaymentCompletedEvent event){
        if(processedEventRepository.existsById(event.eventId())) {
            log.info("Event with id={} already processed", event.eventId());
            return;
        }

        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));

        Optional<Order> orderOptional = orderRepository.findById(event.orderId());
        if(orderOptional.isEmpty()) {
            log.info("Order with id={} not exists", event.orderId());
            return;
        }

        Order order = orderOptional.get();
        order.setLastUpdateDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.addHistory();
        orderRepository.save(order);
    }

    @Transactional
    public void cancelPayment(PaymentCancelledEvent event) {
        if(processedEventRepository.existsById(event.eventId())) {
            log.info("Event with id={} already processed", event.eventId());
            return;
        }

        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));

        Optional<Order> orderOptional = orderRepository.findById(event.orderId());
        if(orderOptional.isEmpty()) {
            log.info("Order with id={} not exists", event.orderId());
            return;
        }

        Order order = orderOptional.get();
        order.setLastUpdateDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.CANCELED);
        log.info("Order with id={} canclled because: {}", event.orderId(), event.reason());
        order.addHistory();
        orderRepository.save(order);
    }

    @Transactional
    public void requestPayment(Order order) {
        OutboxEvent event = new OutboxEvent();
        event.setAgregateId(order.getId().toString());
        event.setAgregateType("Order");
        event.setCreatedAt(LocalDateTime.now());
        event.setEventType("payment.requested");
        event.setPayload(
                toJson(
                     new PaymentRequestedEvent(
                             UUID.randomUUID(),
                             order.getId(),
                             order.getAccountId(),
                             order.getAmount(),
                             LocalDateTime.now()
                     )
                )
        );
        outBoxRepository.save(event);
    }
    private String toJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }
}
