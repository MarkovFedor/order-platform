package com.example.org.events;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


public record ProductChangedEvent(
        UUID eventId,
        Long productId,
        String name,
        Integer price,
        Integer available,
        ProductAction action,
        LocalDateTime occurredAt
) {}