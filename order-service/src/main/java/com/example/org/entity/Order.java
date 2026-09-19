package com.example.org.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name="account_id")
    private Long accountId;

    @Column(name="amount")
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "creation_date")
    private LocalDateTime creationDateTime;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdateDateTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderHistory> orderHistorySet = new HashSet<>();

    public void addHistory() {
        OrderHistory history = new OrderHistory();
        history.setDateTime(LocalDateTime.now());
        history.setOrder(this);
        history.setStatus(this.status);
        orderHistorySet.add(history);
    }
}
