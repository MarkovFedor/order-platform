package com.example.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        Long orderId,
        Long productId,
        Integer quanity,
        LocalDateTime createdAt
) { }
