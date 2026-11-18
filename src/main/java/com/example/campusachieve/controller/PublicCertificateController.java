
package com.example.campusachieve.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.service.AchievementService;

@RestController
@RequestMapping("/api/public/certificates")
public class PublicCertificateController {

    private final AchievementService achievementService;

    public PublicCertificateController(
            AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping("/{achievementId}")
    public ResponseEntity<Resource> viewPublicCertificate(
            @PathVariable Long achievementId) throws IOException {

        Achievement achievement =
                achievementService.getAchievementById(achievementId);

        String storedPath = achievement.getCertificatePath();

        if (storedPath == null || storedPath.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(
                storedPath.replace("\\", "/")
        ).toAbsolutePath().normalize();

        Path uploadDirectory = Paths.get("uploads")
                .toAbsolutePath().normalize();

        if (!filePath.startsWith(uploadDirectory)
                || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + filePath.getFileName() + "\""
                )
                .body(resource);
    }
}
