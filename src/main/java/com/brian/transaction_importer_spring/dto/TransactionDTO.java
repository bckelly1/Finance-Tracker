package com.brian.transaction_importer_spring.dto;

import com.brian.transaction_importer_spring.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Only include non-null fields in the JSON
public class TransactionDTO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Timestamp date;
    private String description;
    private String originalDescription;
    private Double amount;
    private String type;
    private String category;
    private String vendor;
    private String accountName;
    private String mailMessageId;
    private String notes;

    public TransactionDTO(final Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.accountName = transaction.getAccount().getName();
        this.category = transaction.getCategory().getName();
        this.date = transaction.getDate();
        this.vendor = transaction.getMerchant().getName();
        this.description = transaction.getDescription();
        this.originalDescription = transaction.getOriginalDescription();
        this.type = transaction.getTransactionType();
        this.mailMessageId = transaction.getMailMessageId();
        this.notes = transaction.getNotes();
    }
}
