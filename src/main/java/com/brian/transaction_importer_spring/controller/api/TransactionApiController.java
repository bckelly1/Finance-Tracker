package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.TransactionDTO;
import com.brian.transaction_importer_spring.entity.Category;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import com.brian.transaction_importer_spring.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionApiController {

    private TransactionService transactionService;

    private TransactionRepository transactionRepository;

    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public List<TransactionDTO> listTransactions() {
        return transactionRepository.findTop100ByOrderByIdDesc().stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/unknown")
    public List<TransactionDTO> listUnknownTransactions() {
        Category unknown = categoryRepository.findByName("Unknown");
        return transactionRepository.findTransactionByCategory(unknown).stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDTO findTransactionJsonById(@PathVariable final Long id) {
        Transaction transaction = transactionRepository.findById(id).get();
        if (transaction == null) {
            return null;
        }

        return new TransactionDTO(transaction);
    }

    @DeleteMapping("/{id}")
    public void deleteTransactionById(@PathVariable final Long id) {
        transactionRepository.deleteById(id);
    }

    @Deprecated(forRemoval = true)
    @PostMapping("/{id}")
    public TransactionDTO updateTransaction(@PathVariable final Long id, @RequestBody final TransactionDTO transactionDTO) {
        Optional<Transaction> updatedTransaction = transactionService.updateTransaction(id, transactionDTO);
        if (updatedTransaction.isPresent()) {
            return new TransactionDTO(updatedTransaction.get());
        } else {
            throw new NullPointerException("Transaction not found with id " + id);
        }
    }

    @PutMapping("/{id}")
    public TransactionDTO updateTransactionWithPut(@PathVariable final Long id, @RequestBody final TransactionDTO transactionDTO) {
        Optional<Transaction> updatedTransaction = transactionService.updateTransaction(id, transactionDTO);
        if (updatedTransaction.isPresent()) {
            return new TransactionDTO(updatedTransaction.get());
        } else {
            throw new NullPointerException("Transaction not found with id " + id);
        }
    }
}
