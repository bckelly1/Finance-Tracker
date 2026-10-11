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
import java.util.Locale;

@Service
@Log4j2
@RequiredArgsConstructor
public class FidelityPositionSummaryImporter {

    private final AccountRepository accountRepository;

    private final AccountHistoryRepository accountHistoryRepository;

    private final FileWriterService fileWriterService;

    public static final String LAST_SUMMARY_PATH = "last_fidelity_summary.html";


    public void handlePositionSummary(final String summaryEmailText) {
        Document document = Jsoup.parse(summaryEmailText);
        fileWriterService.writeToFile(LAST_SUMMARY_PATH, document.html());
        Elements elements = document.select("td:contains(XXXXX)");
        Element element = elements.getLast();
        String text = element.text();

        String[] tokens = text.split(" ");
        String accountNumber = tokens[1].replaceAll("X", "");

        double balance = Double.parseDouble(text.split("Total ")[1].replace("$", "").replace(",", ""));

        Account account = accountRepository.findByAlias(accountNumber);
        account.setBalance(balance);

        // A bit hacky...
        String rawDate = document.select("td#emailHeader td:contains(, )").last().text();

        // Fri Oct 03, 2025
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH);
        Timestamp timestamp = null;
        try {
            LocalDate dateTime = LocalDate.parse(rawDate, formatter);
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
