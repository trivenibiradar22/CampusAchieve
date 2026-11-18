
package com.example.campusachieve.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");

    public String storeFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a file");
        }

        String originalName = file.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                originalName.lastIndexOf(".")
            ).toLowerCase();
        }

        if (!extension.matches("\\.(pdf|jpg|jpeg|png)")) {
            throw new RuntimeException("Only PDF, JPG and PNG files are allowed");
        }

        Files.createDirectories(uploadDir);

        String fileName = UUID.randomUUID() + extension;
        Path filePath = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }
}
