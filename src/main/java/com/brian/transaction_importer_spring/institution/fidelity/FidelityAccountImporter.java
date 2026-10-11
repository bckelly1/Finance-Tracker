package com.brian.transaction_importer_spring.institution.fidelity;

import com.brian.transaction_importer_spring.dto.AccountToAccountHistory;
import com.brian.transaction_importer_spring.entity.Account;
import com.brian.transaction_importer_spring.entity.AccountHistory;
import com.brian.transaction_importer_spring.repository.AccountHistoryRepository;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.service.FileWriterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Log4j2
@RequiredArgsConstructor
public class FidelityAccountImporter {

    private final AccountRepository accountRepository;

    private final AccountHistoryRepository accountHistoryRepository;

    private final FileWriterService fileWriterService;

    public static final String LAST_ACCOUNT_PATH = "last_fidelity_account.html";

    public void handleBalanceSummary(final String summaryEmailText) {
        Document document = Jsoup.parse(summaryEmailText);
        fileWriterService.writeToFile(LAST_ACCOUNT_PATH, document.html());
        Elements elements = document.select("td:contains(XXXXX)");
        Element element = elements.getLast();
        String text = element.text();

        String[] tokens = text.split(" ");
        String accountNumber = tokens[1].replaceAll("X", "");
        double balance = Double.parseDouble(tokens[6].replace("$", "").replace(",", ""));
        String dateString = tokens[tokens.length - 1].replace(".", "");

        Account account = accountRepository.findByAlias(accountNumber);
        account.setBalance(balance);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Timestamp timestamp = null;
        try {
            LocalDate dateTime = LocalDate.parse(dateString, formatter);
            log.info("Parsed date: {}", dateTime);

            // Convert LocalDate to java.sql.Timestamp
            timestamp = Timestamp.valueOf(dateTime.atStartOfDay());
        } catch (DateTimeParseException e) {
            log.error("Error parsing the date: {}", e.getMessage());
        }

        account.setLastUpdated(timestamp);
        accountRepository.save(account);

        AccountHistory accountHistory = AccountToAccountHistory.parse(account);
        accountHistoryRepository.save(accountHistory);
    }
}
