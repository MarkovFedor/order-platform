package com.example.payment.controller;

import com.example.payment.dto.PaymentResponseDto;
import com.example.payment.service.AccountService;
import com.example.payment.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<PaymentResponseDto> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
