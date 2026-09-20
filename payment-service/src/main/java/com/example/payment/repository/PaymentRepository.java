package com.example.payment.repository;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatusAndNextRetryAtBefore(PaymentStatus status, LocalDateTime dateTime);
}
