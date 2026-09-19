package org.example.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockReservedEvent(UUID eventId, Long orderId, LocalDateTime updatedAt) {}
