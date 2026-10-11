package com.brian.transaction_importer_spring.institution.usbank;

import com.brian.transaction_importer_spring.entity.MailMessage;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import com.brian.transaction_importer_spring.repository.VendorRepository;
import com.brian.transaction_importer_spring.service.CategoryInfererService;
import com.brian.transaction_importer_spring.service.FileWriterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Log4j2
@RequiredArgsConstructor
public class USBankTransactionImporter {

    private final CategoryInfererService categoryInfererService;

    private final CategoryRepository categoryRepository;

    private final VendorRepository vendorRepository;

    private final AccountRepository accountRepository;

    private final FileWriterService fileWriterService;

    public static final String LAST_TRANSACTION_PATH = "last_us_bank_transaction.html";


    public Transaction handleTransactionEmail(final MailMessage mailMessage) {
        return handleTransactions(mailMessage);
    }

    private Transaction handleTransactions(final MailMessage mailMessage) {
        fileWriterService.writeToFile(LAST_TRANSACTION_PATH, mailMessage.getHtml());
        String body = cleanBody(mailMessage.getBody());
        String[] lines = body.split("\n");
        String originalDescription = cleanOriginalDescription(lines[3]);
        String shortDescription = shortenedDescription(originalDescription);

        String cardNumber = findCardNumber(originalDescription);
        Double amount = findAmount(originalDescription);
        String merchant = findMerchant(originalDescription);
        String category = categoryInfererService.getCategory(originalDescription);

        log.info("Card Number: {}", cardNumber);
        log.info("amount: {}", amount);
        log.info("detail: {}\n", shortDescription);

        Transaction transaction = new Transaction();
        transaction.setDate(new Timestamp(Long.parseLong(mailMessage.getHeaders().get("Custom-Epoch")) * 1000L));
        transaction.setMailMessageId(mailMessage.getMessageId());
        transaction.setDescription(shortDescription);
        transaction.setOriginalDescription(originalDescription);
        transaction.setAmount(amount);
        transaction.setTransactionType("Debit");
        transaction.setCategory(categoryRepository.findByName(category));
        transaction.setMerchant(vendorRepository.findOrCreate(merchant));
        transaction.setAccount(accountRepository.findByAlias(cardNumber));
        transaction.setNotes("US Bank");

        return transaction;
    }

    private String cleanBody(final String body) {
        String bodyModified = body;
        while (bodyModified.contains("\r\n")) {
            bodyModified = bodyModified.replace("\r\n", "\n");
        }

        while (bodyModified.contains("\n\n")) {
            bodyModified = bodyModified.replace("\n\n", "\n");
        }

        return bodyModified;
    }

    private String cleanOriginalDescription(final String line) {
        String originalDescription = String.join(" ", line.split(" "));

        while (originalDescription.contains("  ")) {
            originalDescription = originalDescription.replace("  ", " ");
        }

        originalDescription = originalDescription.split(". If ")[0];
        return originalDescription;
    }

    private String shortenedDescription(final String description) {
        String firstSection = description.split("\\. A ")[0];
        return firstSection.split(" at ")[1];
    }

    private Double findAmount(final String description) {
        for (String token : description.split(" ")) {
            if (token.contains("$")) {
                return Double.parseDouble(token.replace("$", ""));
            }
        }

        return null;
    }

    private String findCardNumber(final String description) {
        return description.split("card ending in ")[1];
    }

    private String findMerchant(final String description) {
        String[] tokens = description.split(" ");
        String merchant = "";
        for (int i = 7; i < tokens.length; i++) { // TODO: Could do an overrun
            String token = tokens[i];
            if (merchant.isEmpty()) {
                merchant = token;
            } else {
                merchant = String.join(" ", merchant, token);
            }
        }

        return merchant;
    }
}
