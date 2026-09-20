package com.example.payment.kafka;

import com.example.payment.events.PaymentRequestedEvent;
import com.example.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentsListener {
    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics="payment.requested", groupId = "payment-service")
    public void paymentRequestedListener(PaymentRequestedEvent request) {
        paymentService.createPayment(request);
    }
}
