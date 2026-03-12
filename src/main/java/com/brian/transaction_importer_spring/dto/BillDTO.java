package com.brian.transaction_importer_spring.dto;

import com.brian.transaction_importer_spring.enums.CalendarPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {
    private Long id;
    private String name;
    private String transactionDescription;
    private CalendarPeriod period;
    private Double amount;
    private Timestamp date;
    private boolean isBillArrived;
}
