package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.dto.TransactionDTO;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.opencsv.*;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionImporterService {
    private final TransactionService transactionService;

    public List<TransactionDTO> importTransactions(final String contentBody) {
        // Many CSV formats surround each section with double quotes.
        // Proactively strip them out
        String content = contentBody.replaceAll("\"", "");

        List<TransactionDTO> transactions = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new StringReader(content))) {
            String[] headers = reader.readNext(); // skip header row

            String[] line;
            while ((line = reader.readNext()) != null) {
                TransactionDTO dto = new TransactionDTO();
                dto.setDate(parseDate(line[0]));
                dto.setDescription(line[1]);
                dto.setOriginalDescription(line[2]);
                dto.setAmount(Double.valueOf(line[3]));
                dto.setType(line[4]);
                dto.setCategory(line[5]);
                dto.setVendor(line[6]);
                dto.setAccountName(line[7]);
                dto.setMailMessageId(line[8]);
                dto.setNotes(line[9]);

                // map remaining columns by index
                transactions.add(dto);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        List<TransactionDTO> savedTransactions = saveTransactions(transactions);
        if (savedTransactions.isEmpty() || transactions.size() != savedTransactions.size()) {
            log.error("Error saving transactions");
        }

        return savedTransactions;
    }

    private List<TransactionDTO> saveTransactions(final List<TransactionDTO> transactions) {
        for (TransactionDTO dto : transactions) {
            Optional<Transaction> transaction = transactionService.createTransaction(dto);
            if (transaction.isPresent()) {
                dto.setId(transaction.get().getId());
            } else {
                transactions.remove(dto);
                log.error("Error while creating transaction: {}", dto);
            }
        }

        return transactions;
    }

    private Timestamp parseDate(String date) {
        try {
            final String format = "yyyy-MM-dd HH:mm:ss";
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return Timestamp.valueOf(LocalDateTime.parse(date, formatter));
        }
        catch (DateTimeParseException e) {
            // Try secondary format
            return Timestamp.valueOf(date);
        }
    }
}
