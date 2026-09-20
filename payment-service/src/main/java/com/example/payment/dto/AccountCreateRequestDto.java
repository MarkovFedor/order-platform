package com.example.payment.dto;

import lombok.Data;

@Data
public class AccountCreateRequestDto {
    private String ownerName;
    private Long balance;
}
