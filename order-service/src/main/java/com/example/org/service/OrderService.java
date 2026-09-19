package com.example.org.service;

import com.example.org.DTO.OrderCreate;
import com.example.org.events.OrderCreatedEvent;
import com.example.org.entity.Order;
import com.example.org.entity.OrderStatus;
import com.example.org.entity.OutboxEvent;
import com.example.org.repository.OrderRepository;
import com.example.org.repository.OutBoxRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private OutBoxRepository outboxRepository;

    @Transactional
    public Long createOrder(OrderCreate orderCreate) {
        Order order = new Order();
        order.setProductId(orderCreate.getProductId());
        order.setQuantity(orderCreate.getQuantity());
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDateTime(LocalDateTime.now());
        order.setLastUpdateDateTime(LocalDateTime.now());

        order.addHistory();
        repository.save(order);

        OutboxEvent event = new OutboxEvent();
        event.setAgregateId(order.getId().toString());
        event.setAgregateType("order");
        event.setCreatedAt(order.getCreationDateTime());
        event.setEventType("order.created");
        event.setPayload(
                new OrderCreatedEvent(
                        UUID.randomUUID(),
                        order.getId(),
                        order.getProductId(),
                        order.getQuantity(),
                        order.getCreationDateTime()
                ).toString()
        );
        outboxRepository.save(event);
        return order.getId();
    }
}
