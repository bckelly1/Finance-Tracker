package com.brian.transaction_importer_spring.entity;

import com.brian.transaction_importer_spring.dto.BudgetDTO;
import com.brian.transaction_importer_spring.enums.BudgetPeriod;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Budget {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private BigDecimal limitAmount;

    @Enumerated(EnumType.STRING)
    private BudgetPeriod period; // MONTHLY, WEEKLY, YEARLY

    @ManyToOne
    private Category category; // ties to your existing category model

    @ManyToOne
    private Account account;

    private LocalDate startDate;
}