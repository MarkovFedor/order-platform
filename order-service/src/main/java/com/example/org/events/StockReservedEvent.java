package com.example.org.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockReservedEvent(
        UUID eventId,
        Long orderId,
        LocalDateTime updatedAt
) {}
