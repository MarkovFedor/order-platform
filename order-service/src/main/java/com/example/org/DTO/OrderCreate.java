package com.example.org.DTO;

import lombok.Data;

@Data
public class OrderCreate {
    private Long productId;
    private Long quantity;
}
