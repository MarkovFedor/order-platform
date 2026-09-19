package com.example.org.controller;

import com.example.org.DTO.OrderCreate;
import com.example.org.DTO.OrderShow;
import com.example.org.DTO.ProductViewResponseDto;
import com.example.org.entity.ProductView;
import com.example.org.service.OrderService;
import com.example.org.service.ProductViewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductViewService productViewService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderCreate orderCreate) {
        Long id = orderService.createOrder(orderCreate);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/products")
    public List<ProductViewResponseDto> getAllProducts() {
        return productViewService.getAllProducts();
    }

    @GetMapping("/order/{id}")
    public OrderShow getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
