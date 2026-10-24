package com.brian.transaction_importer_spring.controller;

import com.brian.transaction_importer_spring.institution.fidelity.FidelityAccountImporter;
import com.brian.transaction_importer_spring.institution.fidelity.FidelityPositionSummaryImporter;
import com.brian.transaction_importer_spring.institution.fidelity.FidelityTransactionImporter;
import com.brian.transaction_importer_spring.institution.first_tech.FirstTechAccountImporter;
import com.brian.transaction_importer_spring.institution.first_tech.FirstTechTransactionImporter;
import com.brian.transaction_importer_spring.institution.usbank.USBankTransactionImporter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/file")
public class FileWatcherController {

    @GetMapping("/fidelity/account")
    public String fidelityAccount() {
        Path path = Path.of(FidelityAccountImporter.LAST_ACCOUNT_PATH);
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            return "File path not found, or no emails of this kind have been received";
        }
        return content;
    }

    @GetMapping("/fidelity/summary")
    public String fidelitySummary() {
        Path path = Path.of(FidelityPositionSummaryImporter.LAST_SUMMARY_PATH);
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            return "File path not found, or no emails of this kind have been received";
        }
        return content;
    }

    @GetMapping("/fidelity/transaction")
    public String fidelityTransaction() {
        Path path = Path.of(FidelityTransactionImporter.LAST_TRANSACTION_PATH);
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            return "File path not found, or no emails of this kind have been received";
        }
        return content;
    }

    @GetMapping("/firstTech/account")
    public String firstTechAccount() {
        Path path = Path.of(FirstTechAccountImporter.LAST_ACCOUNT_PATH);
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            return "File path not found, or no emails of this kind have been received";
        }
        return content;
    }

    @GetMapping("/firstTech/transaction")
    public String firstTechTransaction() {
        Path path = Path.of(FirstTechTransactionImporter.LAST_TRANSACTION_PATH);
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            return "File path not found, or no emails of this kind have been received";
        }
        return content;
    }

    @GetMapping("/usBank/transaction")
    public String usBankTransaction() {
        Path path = Path.of(USBankTransactionImporter.LAST_TRANSACTION_PATH);
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            return "File path not found, or no emails of this kind have been received";
        }
        return content;
    }
}
