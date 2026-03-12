package com.brian.transaction_importer_spring.entity;

import com.brian.transaction_importer_spring.enums.CalendarPeriod;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "bill")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String transactionDescription;
    private CalendarPeriod period;
    private double amount;
    private Date date;
    private Date created;
}
