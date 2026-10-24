package com.brian.transaction_importer_spring.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileWriterService {

    public void writeToFile(final String filePath, final String content) {
        Path path = Path.of(filePath);
        try {
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
