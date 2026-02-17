package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.AccountDTO;
import com.brian.transaction_importer_spring.entity.Account;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountApiController {

    private final AccountRepository accountRepository;

    @GetMapping("/")
    public List<AccountDTO> findAllAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AccountDTO findAccountJsonById(@PathVariable final Long id) {
        Account account = accountRepository.findById(id).get();
        if (account == null) {
            return null;
        }

        return new AccountDTO(account);
    }
}
