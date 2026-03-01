package com.brian.transaction_importer_spring.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class TransactionSearchDTO {
    private String description;
    private String category;
    private String vendor;
    private String notes;
    private Timestamp startDate;
    private Timestamp endDate;

    public boolean hasSearchCriteria() {
        return (description != null && !description.trim().isEmpty()) ||
                (category != null && !category.trim().isEmpty()) ||
                (vendor != null && !vendor.trim().isEmpty()) ||
                (notes != null && !notes.trim().isEmpty()) ||
                startDate != null ||
                endDate != null;
    }
}