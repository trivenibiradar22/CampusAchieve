
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.service.AchievementService;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final AchievementService achievementService;

    public CertificateController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping("/{achievementId}")
    public ResponseEntity<Resource> viewCertificate(
            @PathVariable Long achievementId,
            @AuthenticationPrincipal User currentUser)
            throws IOException {

        Achievement achievement =
                achievementService.getAchievementById(achievementId);

        boolean isAdminOrFaculty =
                currentUser.getRole().name().equals("ADMIN")
                || currentUser.getRole().name().equals("FACULTY");

        boolean isOwner =
                achievement.getUser().getId().equals(currentUser.getId());

        if (!isAdminOrFaculty && !isOwner) {
            throw new AccessDeniedException(
                    "You cannot view another student's certificate");
        }

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
                || !Files.exists(filePath)) {
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
