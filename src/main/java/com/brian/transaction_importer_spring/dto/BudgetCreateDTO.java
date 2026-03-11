package com.brian.transaction_importer_spring.dto;

import com.brian.transaction_importer_spring.enums.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCreateDTO {
    private Long id;

    private String name;

    private BigDecimal limitAmount;

    private BudgetPeriod period;

    private Long categoryId;

    private Long accountId;

    private LocalDate startDate;

}
