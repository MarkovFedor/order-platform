package com.example.payment.dto;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private Long orderId;
    private Integer amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponseDto from(Payment payment) {
        PaymentResponseDto response = new PaymentResponseDto();
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }
}
