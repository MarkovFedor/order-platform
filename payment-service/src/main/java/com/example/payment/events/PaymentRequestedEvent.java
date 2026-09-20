package com.example.payment.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentRequestedEvent(
    UUID eventId,
    Long orderId,
    Long accountId,
    Integer amount,
    LocalDateTime createdAt
) {}
