package com.example.org.dto;

import lombok.Data;

@Data
public class OrderCreate {
    private Long productId;
    private Integer quantity;
    private Long accountId;
}
