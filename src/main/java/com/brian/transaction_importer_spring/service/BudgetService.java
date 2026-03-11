package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.dto.BudgetCreateDTO;
import com.brian.transaction_importer_spring.dto.BudgetDTO;
import com.brian.transaction_importer_spring.entity.Budget;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.enums.BudgetPeriod;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.repository.BudgetRepository;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository  accountRepository;

    private final CategoryRepository categoryRepository;

    private final BudgetRepository budgetRepository;

    public boolean deleteBudget(final long id) {
        budgetRepository.deleteById(id);

        return true;
    }

    public BudgetDTO createBudget(final BudgetCreateDTO budgetCreateDTO) {
        Budget budget;
        if (budgetCreateDTO.getId() != null) {
            budget = budgetRepository.findById(budgetCreateDTO.getId()).get();
        }
        else {
            budget = new Budget();
        }

        budget.setName(budgetCreateDTO.getName());
        budget.setLimitAmount(budgetCreateDTO.getLimitAmount());
        budget.setPeriod(budgetCreateDTO.getPeriod());

        categoryRepository.findById(budgetCreateDTO.getCategoryId()).ifPresent(budget::setCategory);
        accountRepository.findById(budgetCreateDTO.getAccountId()).ifPresent(budget::setAccount);

        Budget savedBudget = budgetRepository.save(budget);
        return calculateStatus(savedBudget);
    }

    public List<BudgetDTO> getBudgets() {
        List<Budget> all = budgetRepository.findAll();
        List<BudgetDTO> dtos = new ArrayList<>();
        for (Budget budget : all) {
            BudgetDTO budgetDTO = calculateStatus(budget);
            dtos.add(budgetDTO);
        }

        return dtos;
    }

    public BudgetDTO getBudget(final Long id) {
        Budget budget = budgetRepository.findById(id).orElse(null);
        if (budget == null) {
            return null;
        }

        return calculateStatus(budget);
    }

    public BudgetDTO calculateStatus(final Budget budget) {
        LocalDateTime start = calculateStart(budget.getPeriod());
        LocalDateTime end = calculateEnd();

        List<Transaction> transactions = transactionRepository.findTransactionsByCategoryBetweenDates(
                budget.getCategory(),
                start,
                end
        );
        BigDecimal totalSpent = BigDecimal.ZERO;
        for(Transaction transaction : transactions) {
            totalSpent = totalSpent.add(BigDecimal.valueOf(transaction.getAmount()));
        }

        return toBudgetDTO(budget, totalSpent);
    }

    private LocalDateTime calculateStart(final BudgetPeriod budgetPeriod) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        return switch (budgetPeriod) {
            case MONTHLY -> {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                yield LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
            }
            case WEEKLY -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                yield LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
            }
            case YEARLY -> {
                cal.set(Calendar.DAY_OF_YEAR, 1);
                yield LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
            }
        };
    }

    private LocalDateTime calculateEnd() {
        return LocalDateTime.now();
    }

    private BudgetDTO toBudgetDTO(final Budget budget, final BigDecimal spent) {
        BudgetDTO budgetDTO = new  BudgetDTO();
        budgetDTO.setId(budget.getId());
        budgetDTO.setName(budget.getName());
        budgetDTO.setLimitAmount(budget.getLimitAmount());

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double percentUsed = spent.divide(budget.getLimitAmount(), RoundingMode.HALF_UP)
                .doubleValue() * 100;

        budgetDTO.setRemaining(remaining);
        budgetDTO.setPercentUsed(percentUsed);
        budgetDTO.setOverBudget(percentUsed > 1);
        budgetDTO.setSpent(spent);
        return budgetDTO;
    }
}