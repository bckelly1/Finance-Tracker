package com.brian.transaction_importer_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDTO {
    private Long id;
    private String name;
    private BigDecimal limitAmount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private double percentUsed;
    private boolean isOverBudget;

}
