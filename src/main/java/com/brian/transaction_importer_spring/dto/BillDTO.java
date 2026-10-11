package com.brian.transaction_importer_spring.dto;

import com.brian.transaction_importer_spring.enums.ScheduleType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {
    private Long id;
    private String name;
    private String transactionDescription;
    private ScheduleType scheduleType;
    private String scheduleValue;
    private Double amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Timestamp date;
    private boolean isBillArrived;
}
