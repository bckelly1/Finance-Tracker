package com.brian.transaction_importer_spring.dto;

import com.brian.transaction_importer_spring.enums.CalendarPeriod;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Timestamp date;
    private boolean isBillArrived;
}
