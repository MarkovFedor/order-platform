package com.example.org.DTO;

import com.example.org.entity.Order;
import com.example.org.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderShow {
    private LocalDateTime creationDateTime;
    private LocalDateTime updationDateTime;
    private OrderStatus status;
    private Integer quantity;
    private Long amount;
    private Long accountId;

    public static OrderShow from(Order order) {
        OrderShow orderShow = new OrderShow();
        orderShow.setCreationDateTime(order.getCreationDateTime());
        orderShow.setUpdationDateTime(order.getLastUpdateDateTime());
        orderShow.setStatus(order.getStatus());
        orderShow.setQuantity(order.getQuantity());
        orderShow.setAccountId(order.getAccountId());
        orderShow.setAmount(order.getAmount());
        return orderShow;
    }
}
