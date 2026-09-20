package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String agregateType;
    private String agregateId;
    private String eventType;

    @Column(columnDefinition = "text")
    private String payload;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
