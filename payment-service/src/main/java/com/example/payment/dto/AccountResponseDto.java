package com.example.payment.dto;

import com.example.payment.entity.Account;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountResponseDto {
    private Long id;
    private String ownerName;
    private Long balance;
    private List<PaymentResponseDto> payments = new ArrayList<>();
    public static AccountResponseDto from(Account account) {
        AccountResponseDto response = new AccountResponseDto();
        response.setBalance(account.getBalance());
        response.setId(account.getId());
        response.setOwnerName(account.getOwnerName());
        response.setPayments(
                account.getPayments().stream()
                        .map(PaymentResponseDto::from)
                        .toList()
        );
        return response;
    };
}
