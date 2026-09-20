package com.example.payment.service;

import com.example.payment.dto.AccountCreateRequestDto;
import com.example.payment.dto.AccountResponseDto;
import com.example.payment.dto.AccountUpdateRequestDto;
import com.example.payment.dto.AccountWithdrawRequestDto;
import com.example.payment.entity.Account;
import com.example.payment.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public AccountResponseDto getAccountById(Long id){
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new EntityNotFoundException("Account with id=" + id +" not found");
        }

        return AccountResponseDto.from(accountOptional.get());
    }

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponseDto::from)
                .toList();
    }

    @Transactional
    public AccountResponseDto updateAccountById(Long id, AccountUpdateRequestDto request) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if(accountOptional.isEmpty()) {
            throw new EntityNotFoundException("Account with id=" + id +" not found");
        }
        Account account = accountOptional.get();
        if(request.getOwnerName() != null) {
            account.setOwnerName(request.getOwnerName());
        };
        return AccountResponseDto.from(account);
    }

    @Transactional
    public Long createAccount(AccountCreateRequestDto request) {
        Account account = new Account();
        account.setBalance(request.getBalance());
        account.setOwnerName(request.getOwnerName());
        accountRepository.save(account);
        return account.getId();
    }

    @Transactional
    public AccountResponseDto withdrawAccount(Long id, AccountWithdrawRequestDto request) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if(accountOptional.isEmpty()) {
            throw new EntityNotFoundException("Account with id=" + id +" not found");
        }
        Account account = accountOptional.get();

        if(request.getSum() > 0) {
            account.setBalance(account.getBalance() + request.getSum());
        }

        accountRepository.save(account);
        return AccountResponseDto.from(account);
    }
}
