package com.example.payment.controller;

import com.example.payment.dto.AccountCreateRequestDto;
import com.example.payment.dto.AccountResponseDto;
import com.example.payment.dto.AccountUpdateRequestDto;
import com.example.payment.service.AccountService;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/account")
    public List<AccountResponseDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/account/{id}")
    public AccountResponseDto getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping("/account")
    public Long createAccount(@RequestBody AccountCreateRequestDto request) {
        return accountService.createAccount(request);
    }

    @PutMapping("/account/{id}")
    public AccountResponseDto updateAccountById(@PathVariable Long id, @RequestBody AccountUpdateRequestDto request) {
        return accountService.updateAccountById(id, request);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
