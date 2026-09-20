package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class PaymentRetryScheduler {

    private final PaymentRepository paymentRepository;

    private final PaymentService paymentService;

    @Scheduled(fixedDelay = 60000)
    public void retryFailedPayment() {
        List<Payment> paymentsToRetry = paymentRepository
                .findByStatusAndNextRetryAtBefore(PaymentStatus.RETRYING, LocalDateTime.now());
        paymentsToRetry.forEach(payment -> paymentService.processPayment(payment));
    }
}
