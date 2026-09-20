package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="owner_name")
    private String ownerName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "balance")
    private Long balance;

    public void addPayment(Payment payment) {
        payments.add(payment);
    }
}
