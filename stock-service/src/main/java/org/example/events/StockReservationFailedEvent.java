package org.example.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockReservationFailedEvent(UUID eventId, Long orderId, String reason, LocalDateTime updatedAt) {}
