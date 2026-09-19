package com.example.org.DTO;

import com.example.org.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderShow {
    private LocalDateTime creationDateTime;
    private LocalDateTime updationDateTime;
    private String description;
    private OrderStatus status;
}
