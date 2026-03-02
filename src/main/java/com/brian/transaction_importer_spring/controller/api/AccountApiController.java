package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.AccountDTO;
import com.brian.transaction_importer_spring.entity.Account;
import com.brian.transaction_importer_spring.entity.Institution;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountApiController {

    private final AccountRepository accountRepository;

    private final InstitutionRepository institutionRepository;

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

    @PutMapping("/{id}")
    public AccountDTO updateAccount(@PathVariable final Long id, @RequestBody final AccountDTO accountDTO) {
        Account account = accountRepository.findById(id).get();
        if (account == null) {
            return null;
        }

        account.setName(accountDTO.getName());
        account.setBalance(accountDTO.getBalance());
        account.setAlias(accountDTO.getAlias());
        account.setType(accountDTO.getType());
        account.setLastUpdated(accountDTO.getLastUpdated());
        return new AccountDTO(accountRepository.save(account));
    }

    @PostMapping
    public AccountDTO createAccount(@RequestBody final AccountDTO accountDTO) {
        Institution institution = institutionRepository.findByName(accountDTO.getInstitutionName()).getFirst();

        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setBalance(accountDTO.getBalance());
        account.setAlias(accountDTO.getAlias());
        account.setType(accountDTO.getType());
        account.setLastUpdated(accountDTO.getLastUpdated());
        account.setInstitution(institution);

        return new AccountDTO(accountRepository.save(account));
    }
}
