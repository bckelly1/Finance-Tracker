package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.dto.TransactionDTO;
import com.brian.transaction_importer_spring.entity.Category;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

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
}
