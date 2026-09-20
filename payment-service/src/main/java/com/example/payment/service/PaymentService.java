package com.example.payment.service;

import com.example.events.PaymentCancelledEvent;
import com.example.events.PaymentCompletedEvent;
import com.example.events.PaymentRequestedEvent;
import com.example.payment.dto.PaymentResponseDto;
import com.example.payment.entity.*;
import com.example.payment.repository.AccountRepository;
import com.example.payment.repository.OutBoxRepository;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.repository.ProcessedEventRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {
    private static final int MAX_RETRIES = 3;
    private static final Duration[] RETRY_DELAYS = {
            Duration.ofMinutes(1),
            Duration.ofMinutes(5),
            Duration.ofMinutes(15)
    };
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Autowired
    private OutBoxRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public PaymentResponseDto getPaymentById(Long id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if(optionalPayment.isEmpty()) {
            throw new EntityNotFoundException("Payment with id = " + id + " not found");
        }

        return PaymentResponseDto.from(optionalPayment.get());
    }

    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponseDto::from)
                .toList();
    }

    @Transactional
    public void createPayment(PaymentRequestedEvent request) {
        if(processedEventRepository.existsById(request.eventId())) {
            log.info("Event with id={} already processed", request.eventId());
        }
        processedEventRepository.save(new ProcessedEvent(request.eventId(), LocalDateTime.now()));

        Optional<Account> accountOptional = accountRepository.findById(request.accountId());
        if(accountOptional.isEmpty()) {
            createOutboxEvent(
                    request.orderId().toString(),
                    "Order",
                    LocalDateTime.now(),
                    "payment.cancelled",
                    toJson(
                            new PaymentCancelledEvent(
                                    UUID.randomUUID(),
                                    request.orderId(),
                                    request.accountId(),
                                    request.amount(),
                                    LocalDateTime.now(),
                                    "Account with id not found"
                            ))
            );
            return;
        }

        Account account = accountOptional.get();
        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setStatus(PaymentStatus.REQUESTED);
        payment.setCreatedAt(LocalDateTime.now());
        account.addPayment(new Payment());

        accountRepository.save(account);
        processPayment(payment);
    }

    public void createOutboxEvent(String aggregateId, String aggregateType, LocalDateTime createdAt, String eventType, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAgregateId(aggregateId);
        event.setAgregateType(aggregateType);
        event.setCreatedAt(createdAt);
        event.setEventType(eventType);
        event.setPayload(payload);
        outboxRepository.save(event);
    }

    @Transactional
    public void processPayment(Payment payment) {
        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.CANCELLED) {
            return;
        }

        charge(payment);
    }

    public void charge(Payment payment) {
        Account account = payment.getAccount();
        if(account.getBalance() >= payment.getAmount()) {
            account.setBalance(account.getBalance() - payment.getAmount());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setUpdatedAt(LocalDateTime.now());
            createOutboxEvent(
                    payment.getOrderId().toString(),
                    "Order",
                    LocalDateTime.now(),
                    "payment.accepted",
                    toJson(
                            new PaymentCompletedEvent(
                                    UUID.randomUUID(),
                                    payment.getOrderId(),
                                    payment.getAccount().getId(),
                                    payment.getAmount(),
                                    LocalDateTime.now()
                            ))
            );
        } else {
            payment.setStatus(PaymentStatus.RETRYING);
            handleRetryableFailure(payment);
        }
    }

    private void handleRetryableFailure(Payment payment) {
        payment.setRetryCount(payment.getRetryCount() + 1);

        if (payment.getRetryCount() >= MAX_RETRIES) {
            payment.setStatus(PaymentStatus.CANCELLED);
            createOutboxEvent(
                    payment.getOrderId().toString(),
                    "Order",
                    LocalDateTime.now(),
                    "payment.cancelled",
                    toJson(
                            new PaymentCancelledEvent(
                                    UUID.randomUUID(),
                                    payment.getOrderId(),
                                    payment.getAccount().getId(),
                                    payment.getAmount(),
                                    LocalDateTime.now(),
                                    "Not enough money on balance"
                            ))
            );
        } else {
            payment.setStatus(PaymentStatus.RETRYING);
            payment.setNextRetryAt(LocalDateTime.now().plus(RETRY_DELAYS[payment.getRetryCount() - 1]));
        }
    }

    private String toJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }
}
