package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.BudgetCreateDTO;
import com.brian.transaction_importer_spring.dto.BudgetDTO;
import com.brian.transaction_importer_spring.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/{id}")
    public BudgetDTO getStatus(@PathVariable Long id) {
        return budgetService.getBudget(id);
    }

    @GetMapping("/all")
    public List<BudgetDTO> getAllBudgets() {
        return budgetService.getBudgets();
    }

    @PostMapping
    public BudgetDTO createBudget(@RequestBody BudgetCreateDTO request) {
        return budgetService.createBudget(request);
    }

    @PutMapping
    public BudgetDTO updateBudget(@RequestBody BudgetCreateDTO request) {
        return budgetService.createBudget(request);
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
    }
}