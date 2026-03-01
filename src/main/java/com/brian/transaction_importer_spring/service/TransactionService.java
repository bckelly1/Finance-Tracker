package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.dto.TransactionDTO;
import com.brian.transaction_importer_spring.dto.TransactionSearchDTO;
import com.brian.transaction_importer_spring.entity.Account;
import com.brian.transaction_importer_spring.entity.Category;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.entity.Vendor;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import com.brian.transaction_importer_spring.repository.VendorRepository;
import com.brian.transaction_importer_spring.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    private final AccountRepository accountRepository;

    private final VendorRepository vendorRepository;

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> searchTransactions(TransactionSearchDTO searchDTO) {
        if (!searchDTO.hasSearchCriteria()) {
            return transactionRepository.findTop100ByOrderByIdDesc().stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
        }

        Specification<Transaction> spec = TransactionSpecification.searchTransactions(searchDTO);
        return transactionRepository.findAll(spec).stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<Transaction> updateTransaction(final Long id, final TransactionDTO transactionDTO) {
        Category category = categoryRepository.findByName(transactionDTO.getCategory());

        return transactionRepository.findById(id).map(transaction -> {
            transaction.setCategory(category);
            transaction.setAmount(transactionDTO.getAmount());
            transaction.setDescription(transactionDTO.getDescription());
            transaction.setNotes(transactionDTO.getNotes());
            transaction.setTransactionType(transactionDTO.getType());
            return transactionRepository.save(transaction);
        });
    }

    public Optional<Transaction> createTransaction(final TransactionDTO transactionDTO) {
        Category category = categoryRepository.findByName(transactionDTO.getCategory());

        // TODO: This could be a bug, there needs to be a better way to handle this
        Account account = accountRepository.findByName(transactionDTO.getAccountName()).getFirst();

        Vendor vendor = vendorRepository.findByName(transactionDTO.getVendor()).orElse(null);

        Transaction transaction = new Transaction();
        transaction.setDate(transactionDTO.getDate());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setOriginalDescription(transactionDTO.getOriginalDescription());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionType(transactionDTO.getType());
        transaction.setCategory(category);
        transaction.setMerchant(vendor);
        transaction.setAccount(account);
        transaction.setNotes(transactionDTO.getNotes());
        transaction.setMailMessageId("Manual Create");

        transaction = transactionRepository.save(transaction);

        // TODO: Check if this returns the ID
        return Optional.of(transaction);
    }
}
