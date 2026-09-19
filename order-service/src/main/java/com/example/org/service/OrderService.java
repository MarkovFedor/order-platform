package com.example.org.service;

import com.example.org.DTO.OrderCreate;
import com.example.org.DTO.OrderShow;
import com.example.org.entity.*;
import com.example.org.events.OrderCreatedEvent;
import com.example.org.events.StockReservationFailedEvent;
import com.example.org.events.StockReservedEvent;
import com.example.org.repository.OrderRepository;
import com.example.org.repository.OutBoxRepository;
import com.example.org.repository.ProcessedEventRepository;
import com.example.org.repository.ProductViewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private OutBoxRepository outboxRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductViewRepository productViewRepository;

    @Transactional
    public Long createOrder(OrderCreate orderCreate) {
        Order order = new Order();
        order.setProductId(orderCreate.getProductId());
        order.setQuantity(orderCreate.getQuantity());
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDateTime(LocalDateTime.now());
        order.setLastUpdateDateTime(LocalDateTime.now());
        order.setAccountId(orderCreate.getAccountId());
        Optional<ProductView> product = productViewRepository.findById(orderCreate.getProductId());
        if(product.isEmpty()) {
            throw new EntityNotFoundException("Not found product");
        }

        Long price = product.get().getPrice();
        order.setAmount(orderCreate.getQuantity()*price);
        order.addHistory();
        repository.save(order);

        OutboxEvent event = new OutboxEvent();
        event.setAgregateId(order.getId().toString());
        event.setAgregateType("order");
        event.setCreatedAt(order.getCreationDateTime());
        event.setEventType("order.created");
        event.setPayload(toJson(
                new OrderCreatedEvent(
                        UUID.randomUUID(),
                        order.getId(),
                        order.getProductId(),
                        order.getQuantity(),
                        order.getCreationDateTime()
                ))
        );
        outboxRepository.save(event);
        return order.getId();
    }

    public OrderShow getOrderById(Long id) {
        Optional<Order> order = repository.findById(id);
        if(order.isPresent()) {
            return OrderShow.from(order.get());
        }
        throw new EntityNotFoundException("Order not found");
    }

    @Transactional
    public void markOrderReserved(StockReservedEvent event) {
        log.info("Starting to mark order as reserved");
        if(processedEventRepository.existsById(event.eventId())) {
            log.info("Event with id={} already processed", event.eventId());
            return;
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));

        Optional<Order> order = repository.findById(event.orderId());
        if(order.isEmpty()) {
            log.info("Order with id={} not exists", event.orderId());
            return;
        }

        order.get().setStatus(OrderStatus.STOCK_RESERVED);
        log.info("Order with id={} marked as reserved", order.get().getId());
    }

    @Transactional
    public void markOrderFailedToReserve(StockReservationFailedEvent event) {
        if(processedEventRepository.existsById(event.eventId())) {
            log.info("Event with id={} already processed", event.eventId());
            return;
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));

        Optional<Order> order = repository.findById(event.orderId());
        if(order.isEmpty()) {
            log.info("Order with id={} not exists", event.orderId());
            return;
        }

        order.get().setStatus(OrderStatus.CANCELED);
    }
    private String toJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }
}
