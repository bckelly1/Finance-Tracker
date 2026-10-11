package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.entity.MailMessage;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.enums.KnownInstitution;
import com.brian.transaction_importer_spring.institution.fidelity.FidelityTransactionImporter;
import com.brian.transaction_importer_spring.institution.first_tech.FirstTechTransactionImporter;
import com.brian.transaction_importer_spring.institution.usbank.USBankTransactionImporter;
import com.brian.transaction_importer_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionParserService {

    private final FidelityTransactionImporter fidelityParser;

    private final TransactionRepository transactionRepository;

    private final FirstTechTransactionImporter firstTechTransactionImporter;

    private final USBankTransactionImporter usBankTransactionImporter;


    public Transaction[] parseTransaction(final MailMessage mailMessage) {
        KnownInstitution knownInstitution = parseInstitution(mailMessage);
        Transaction[] transactions = null;
        if (knownInstitution == KnownInstitution.FIDELITY) {
            Transaction transaction = fidelityParser.handleTransaction(mailMessage);
            transactions = new Transaction[]{transaction};
        } else if (knownInstitution == KnownInstitution.FIRST_TECH) {
            transactions = firstTechTransactionImporter.handleTransactionEmail(mailMessage);
        } else if (knownInstitution == KnownInstitution.US_BANK) {
            Transaction transaction = usBankTransactionImporter.handleTransactionEmail(mailMessage);
            transactions = new Transaction[]{transaction};
        }

        assert transactions != null && transactions.length > 0;
        transactionRepository.saveAll(Arrays.asList(transactions));
        return transactions;
    }

    private KnownInstitution parseInstitution(final MailMessage mailMessage) {
        String fromField = mailMessage.getHeaders().get("From").toLowerCase();
        if (fromField.contains("fidelity")) {
            return KnownInstitution.FIDELITY;
        } else if (fromField.contains("first tech")) {
            return KnownInstitution.FIRST_TECH;
        } else if (fromField.contains("u.s. bank")) {
            return KnownInstitution.US_BANK;
        } else if (fromField.contains("home depot")) {
            return KnownInstitution.HOME_DEPOT;
        }
        throw new IllegalStateException("Unknown institution: " + fromField);
    }
}
