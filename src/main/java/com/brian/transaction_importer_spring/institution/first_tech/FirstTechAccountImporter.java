package com.brian.transaction_importer_spring.institution.first_tech;

import com.brian.transaction_importer_spring.dto.AccountToAccountHistory;
import com.brian.transaction_importer_spring.entity.Account;
import com.brian.transaction_importer_spring.entity.AccountHistory;
import com.brian.transaction_importer_spring.repository.AccountHistoryRepository;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.service.FileWriterService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Log4j2
@RequiredArgsConstructor
public class FirstTechAccountImporter {

    private final AccountRepository accountRepository;

    private final AccountHistoryRepository accountHistoryRepository;

    private final FileWriterService fileWriterService;

    public static final String LAST_ACCOUNT_PATH = "last_first_tech_account.html";

    // In the main body of the email, figure out which account we are extracting the balance of.
    public MinimumAccount[] parseAccountInfo(final Document document) {
        String title = document.body().select("h1#title").text().strip();
        log.info("Title: {}", title);
        Elements accounts = document.body().select("strong");
        MinimumAccount[] parsedAccounts = new MinimumAccount[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            Element account = accounts.get(i);
            parsedAccounts[i] = parseAccount(account);
        }

        return parsedAccounts;
    }


    // For each account in the email, extract the account details
    MinimumAccount parseAccount(final Element account) {
        Element accountParent = account.parent();
        String accountName = account.text().strip().split(" - ")[0];
        String accountNumber = account.text().strip().split(" - ")[1].replace("*", "").replace("#", "");
        String balance = accountParent.text().split("Current Balance: ")[1].split("Available Balance")[0].replace("$", "").replace(",", "");

        log.info("Account Name: {}", accountName);
        log.info("Account Number: {}", accountNumber);
        log.info("Balance: {}", balance);

        return new MinimumAccount(accountName, accountNumber, Double.parseDouble(balance));
    }

    // Extract all account details from the balance summary email
    public void handleBalanceSummary(final String text) {
        Document soup = Jsoup.parse(text);
        fileWriterService.writeToFile(LAST_ACCOUNT_PATH, soup.html());
        MinimumAccount[] minimumAccounts = parseAccountInfo(soup);
        for (MinimumAccount minimumAccount : minimumAccounts) {
            Account account = accountRepository.findByAlias(minimumAccount.getNumber());
            account.setBalance(minimumAccount.getBalance());
            account.setLastUpdated(new Timestamp(System.currentTimeMillis()));
            accountRepository.save(account);

            AccountHistory accountHistory = AccountToAccountHistory.parse(account);
            accountHistoryRepository.save(accountHistory);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class MinimumAccount {
        private String name;
        private String number;
        private Double balance;
    }
}
