package com.example.org.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockReservationFailedEvent(UUID eventId, Long orderId, String reason, LocalDateTime updatedAt) {}
