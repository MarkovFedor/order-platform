package com.example.org.kafka;

import com.example.org.events.PaymentCancelledEvent;
import com.example.org.events.PaymentCompletedEvent;
import com.example.org.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class PaymentEventsListener {
    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics = "payment.accepted", groupId = "order-service")
    public void paymentAcceptedProcess(PaymentCompletedEvent event) {
        paymentService.acceptPayment(event);
    }

    @KafkaListener(topics = "payment.cancelled", groupId = "order-service")
    public void paymentCancelledProcess(PaymentCancelledEvent event) {
        paymentService.cancelPayment(event);
    }
}
