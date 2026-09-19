package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="reservations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="order_id")
    private Long order_id;

    @Column(name="quantity")
    private Long quntity;

    @Column(name="product_id")
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ReservationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
