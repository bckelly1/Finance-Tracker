package com.brian.transaction_importer_spring.institution.first_tech;

import com.brian.transaction_importer_spring.entity.MailMessage;
import com.brian.transaction_importer_spring.entity.Transaction;
import com.brian.transaction_importer_spring.repository.AccountRepository;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import com.brian.transaction_importer_spring.repository.VendorRepository;
import com.brian.transaction_importer_spring.service.CategoryInfererService;
import com.brian.transaction_importer_spring.service.FileWriterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class FirstTechTransactionImporter {

    private final CategoryInfererService categoryInfererService;

    private final CategoryRepository categoryRepository;

    private final VendorRepository vendorRepository;

    private final AccountRepository accountRepository;

    private final FileWriterService fileWriterService;

    public static final String LAST_TRANSACTION_PATH = "last_first_tech_transaction.html";

    // With Bank accounts, you have to determine if the money is going in (credit) or going out (debit)
    //    For First Tech, you can either determine that by looking for the parentheses () or the Credit/Debit label
    public String determineTransactionStyle(final Document document) {
        String token = document.select("div.transactions-table-header").text().strip().split(" ")[0];
        if (token.equals("Deposits")) {
            return "Credit";
        } else if (token.equals("Withdrawals")) {
            return "Debit";
        } else {
            //TODO: Problem !
            log.error("Could not determine transaction style!");
            return "Unknown";
        }
    }


    // Is the money just moving between two user-owned accounts? While that does count as a Credit/Debit, in the grand scheme
    //   Of things, it doesn't affect the budget.  Mark the transaction as a transfer and it will be ignored.
    public boolean isTransfer(final String title) {
        if (title.contains("Regular Payment Transfer")) {
            return false;
        } else if (title.contains("Transfer")) {
            return true;
        } else {
            return isCreditCardPayment(title);
        }
    }


    // Credit card payments do and don't matter. They do matter because the money is literally leaving your bank account. On
    //   The other hand, you already spent the money, so the money is "gone" already, so we can think of the credit card
    //   Payment more as a transfer than as "money going out".  Let the rage debate commence.
    public boolean isCreditCardPayment(final String title) {
        // Fidelity is a bit obnoxious about this. No email on payments. Have to infer it when the money goes out.
        return (title.contains("CARDMEMBER SERV")) && title.contains("WEB PYMT");
    }

    // Example input: Deposit Transfer From ******1234
    // Output: 1234
    public String transferSourceAccount(final String title) {
        String[] tokens = title.split(" ");
        for (String token : tokens) {
            if (token.contains("*")) {
                return token.replace("*", "");
            }
        }
        return "Unknown Source Account";
    }

    public String determineSourceAccount(final boolean isTransfer, final String transactionDetailsOriginal, final String merchant) {
        if (isTransfer) {
            if (isCreditCardPayment(transactionDetailsOriginal)) {
                return merchant;
            }
            return transferSourceAccount(transactionDetailsOriginal);
        }
        return merchant;
    }

    // Main handling of the transaction email. Read the transaction and extract transaction details from the text.
    public Transaction[] handleTransactionEmail(final MailMessage mailMessage) {
        return parseHtml(mailMessage);
    }

    public Transaction handleTransactionRow(final Element element, final MailMessage mailMessage, final String accountNumber) {
        Timestamp transactionDate = new Timestamp(Long.parseLong(mailMessage.getHeaders().get("Custom-Epoch")) * 1000L);
        String messageId = mailMessage.getHeaders().get("Message-ID");
        String transactionDetailsOriginal = element.select("td.details").text().strip();
        String description = extractDescription(transactionDetailsOriginal);
        double amount = extractAmount(element);
        String transactionType = extractTransactionType(element);
        String merchant = extractMerchant(transactionDetailsOriginal);
        boolean transfer = isTransfer(transactionDetailsOriginal);
        String category = categoryInfererService.getCategory(transactionDetailsOriginal);
        String sourceAccount = determineSourceAccount(transfer, transactionDetailsOriginal, merchant);

        log.info("Info:");
        log.info("\tDate: {}", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(transactionDate));
        log.info("\tDetail: {}", merchant);
        log.info("\tAmount: {}", amount);

        Transaction transaction = new Transaction();
        transaction.setDate(transactionDate);
        transaction.setMailMessageId(messageId);
        transaction.setDescription(description);
        transaction.setOriginalDescription(transactionDetailsOriginal);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setCategory(categoryRepository.findByName(category));
        if (transfer) {
            transaction.setMerchant(vendorRepository.findOrCreate(sourceAccount)); // source_account if transfer else merchant
        } else {
            transaction.setMerchant(vendorRepository.findOrCreate(merchant)); // source_account if transfer else merchant
        }
        transaction.setAccount(accountRepository.findByAlias(accountNumber));
        transaction.setNotes("First Tech");

        return transaction;
    }


    // Parse the email's HTML text and extract relevant transaction info from it.
    public Transaction[] parseHtml(final MailMessage mailMessage) {
        Document soup = Jsoup.parse(mailMessage.getHtml());
        fileWriterService.writeToFile(LAST_TRANSACTION_PATH, soup.html());
        String title = soup.body().select("h1#title").text().strip();
        String accountName = soup.body().select("strong").getFirst().text().strip().split(" - ")[0];
        String accountNumber = soup.body().select("strong").getFirst().text().strip().split(" - ")[1].replace("*", "");
        String balance = soup.body().select("p:contains(Balance)").getFirst().text().split("Balance: ")[1].strip();

        log.info("Title: {}", title);
        log.info("Account Name: {}", accountName);
        log.info("Account Number: {}", accountNumber);
        log.info("Balance: {}", balance);

        String transactionStyle = determineTransactionStyle(soup);
        Elements transactionElements = soup.body().select("tr.transaction-row");
        Transaction[] transactions = new Transaction[transactionElements.size()];
        for (int i = 0; i < transactionElements.size(); i++) {
            Element transactionElement = transactionElements.get(i);
            Transaction transaction = handleTransactionRow(transactionElement, mailMessage, accountNumber);
            if (!Objects.equals(transactionStyle, transaction.getTransactionType())) {
                log.error("Transaction style {} does not match parsed transaction type from transaction {}", transactionStyle, transaction.getTransactionType());
            }
            if (transaction.getMerchant() == null) {
                transaction.setMerchant(vendorRepository.findOrCreate(accountName));
                transaction.setAccount(accountRepository.findByAlias(accountNumber));  // TODO: possibly redundant?
            }
            transactions[i] = transaction;
        }
        return transactions;
    }

    private double extractAmount(final Element element) {
        String transactionAmount = element.select("td.trans-amount").text().strip().replace("$", "").replace(",", "");
        double amount;
        if (transactionAmount.contains("(")) {
            amount = Double.parseDouble(transactionAmount.replace("(", "").replace(")", ""));
        } else if (transactionAmount.contains("-")) {
            amount = Double.parseDouble(transactionAmount.replace("-", ""));
        } else {
            amount = Double.parseDouble(transactionAmount);
        }

        return amount;
    }

    private String extractTransactionType(final Element element) {
        String transactionAmount = element.select("td.trans-amount").text().strip().replace("$", "").replace(",", "");
        String transactionType;
        if (transactionAmount.contains("(")) {
            transactionType = "Debit";
        } else if (transactionAmount.contains("-")) {
            transactionType = "Debit";
        } else {
            transactionType = "Credit";
        }

        return transactionType;
    }

    private String extractMerchant(final String transactionDetailsOriginal) {
        String merchant;
        if (transactionDetailsOriginal.contains("Transfer") && transactionDetailsOriginal.contains("Dividend")) {
            merchant = "First Tech";
        } else if (transactionDetailsOriginal.contains("CARDMEMBER SERV")) {
            merchant = "Fidelity";
        } else {
            String[] transactionTokens = transactionDetailsOriginal.split(" ");
            merchant = String.join(" ", Arrays.copyOfRange(transactionTokens, 2, transactionTokens.length));
        }

        return merchant;
    }

    private String extractDescription(final String transactionDetailsOriginal) {
        String[] transactionTokens = transactionDetailsOriginal.split(" ");
        return String.join(" ", Arrays.copyOfRange(transactionTokens, 2, transactionTokens.length));
    }
}
