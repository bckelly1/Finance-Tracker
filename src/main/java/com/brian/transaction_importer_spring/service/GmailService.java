package com.brian.transaction_importer_spring.service;

import com.brian.transaction_importer_spring.config.MailConfig;
import com.brian.transaction_importer_spring.entity.MailMessage;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.MessageIDTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SubjectTerm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailService {
    private final MailConfig mailConfig;

    private Store store;
    private Session session;

    private Store getStore() {
        if (store == null || !store.isConnected()) {
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", "imaps");

            session = Session.getInstance(properties, null);
            try {
                store = session.getStore("imaps");
                store.connect(mailConfig.getHost(), mailConfig.getUsername(), mailConfig.getPassword());
            } catch (final MessagingException e) {
                log.error("Error connecting to imaps server", e);
                throw new RuntimeException(e);
            }
            log.info("Created new Gmail IMAP connection");
        }
        return store;
    }

    private Folder openFolder(final String label, final int mode) {
        Folder folder = null;
        try {
            folder = getStore().getFolder(label);
            folder.open(mode);
        } catch (MessagingException e) {
            log.error("Error opening folder", e);
            throw new RuntimeException(e);
        }
        return folder;
    }

    public MailMessage[] getUnreadMessages(final String filter, final String label) {
        // Gmail IMAP properties
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");

        try {
            // Open inbox folder
            Folder inbox = openFolder(label, Folder.READ_ONLY);

            // Create a search term for unread messages
            SearchTerm unreadTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

            // Create a search term for messages with subject "filter"
            SearchTerm subjectTerm = new SubjectTerm(filter);

            // Combine the search terms using the AndTerm
            SearchTerm searchTerm = new AndTerm(unreadTerm, subjectTerm);

            Message[] messages = inbox.search(searchTerm);
            MailMessage[] mailMessages = parseMailMessages(messages, label);

            // Close resources
            inbox.close(false);

            return mailMessages;
        } catch (MessagingException e) {
            // Handle exceptions
            log.error("Error getting unread messages", e);
            return new MailMessage[0];
        }
    }

    // When store.close() is called, the messages in the array are no longer accessible. We need to copy out (or process)
    //   the messages, so we can work on them independently.
    private MailMessage[] parseMailMessages(final Message[] messages, final String label) {
        MailMessage[] mailMessages = new MailMessage[messages.length];
        for (int i = 0; i < messages.length; i++) {
            MailMessage mailMessage = parseMailMessage(messages[i], label);
            mailMessages[i] = mailMessage;
        }
        return mailMessages;
    }

    private MailMessage parseMailMessage(final Message message, final String label) {
        try {
            Map<String, String> headers = parseHeaders(message);
            String from = headers.get("From");
            String to = headers.get("To");
            String subject = headers.get("Subject");
            String body = getTextBody(message);
            String html = getHtmlBody(message);
            String messageId = headers.get("Message-ID").replace("<", "").replace(">", "");
            return new MailMessage(from, to, subject, body, html, messageId, label, headers);
        } catch (IOException | MessagingException e) {
            log.error("Error parsing mail message", e);
            return null;
        }
    }

    private String getTextBody(final Message message) throws IOException, MessagingException {
        if (message.getContent().getClass() == MimeMultipart.class) {
            MimeMultipart content = ((MimeMultipart) message.getContent());
            int length = content.getCount();
            for (int i = 0; i < length; i++) {
                if (content.getBodyPart(i).isMimeType("text/plain")) {
                    return content.getBodyPart(i).getContent().toString();
                }
            }
        }
        return message.getContent().toString();
    }

    private String getHtmlBody(final Message message) throws IOException, MessagingException {
        if (message.getContent().getClass() == MimeMultipart.class) {
            MimeMultipart content = ((MimeMultipart) message.getContent());
            int length = content.getCount();
            for (int i = 0; i < length; i++) {
                if (content.getBodyPart(i).isMimeType("text/html")) {
                    return content.getBodyPart(i).getContent().toString();
                }
            }
        }
        return message.getContent().toString();
    }

    private Map<String, String> parseHeaders(final Message message) throws MessagingException {
        Map<String, String> headers = new HashMap<>();
        Iterator<Header> iterator = message.getAllHeaders().asIterator();
        while (iterator.hasNext()) {
            Header header = iterator.next();
            if (header.getName().equals("ARC-Seal")) {
                String epoch = header.getValue().split("; ")[2].split("=")[1];
                headers.put("Custom-Epoch", epoch);
            } else {
                headers.put(header.getName(), header.getValue());
            }
        }
        return headers;
    }

    public void markAsRead(final MailMessage mailMessage) {
        // Gmail IMAP properties
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");

        try {
            // Open inbox folder
            Folder inbox = openFolder(mailMessage.getLabel(), Folder.READ_WRITE);

            //Should only be one record
            SearchTerm searchTerm = new MessageIDTerm(mailMessage.getMessageId());
            Message[] findMessages = inbox.search(searchTerm);
            for (Message message : findMessages) {
                message.setFlag(Flags.Flag.SEEN, true);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

