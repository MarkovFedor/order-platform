package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String agregateType;
    private String agregateId;
    private String eventType;

    @Column(columnDefinition = "text")
    private String payload;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    public static OutboxEvent of(String aggregateType,
                                 String aggregateId,
                                 String eventType,
                                 String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAgregateType(aggregateType);
        event.setAgregateId(aggregateId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }
}
