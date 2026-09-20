package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "order_id", unique = true)
    private Long orderId;

    @Column(name = "amount")
    private Integer amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name="payment_status")
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name="account_id", nullable = false)
    private Account account;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="retry_count")
    private Integer retryCount;

    @Column(name="next_retry_at")
    private LocalDateTime nextRetryAt;
}
