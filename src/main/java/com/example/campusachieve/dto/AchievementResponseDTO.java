
package com.example.campusachieve.dto;

import java.time.LocalDate;

import com.example.campusachieve.entity.VerificationStatus;

public class AchievementResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private LocalDate achievementDate;
    private VerificationStatus verificationStatus;
    private Long userId;
    private String certificatePath;
    public AchievementResponseDTO() {
    }

    public AchievementResponseDTO(Long id, String title,
            String description, String category,
            LocalDate achievementDate,
            VerificationStatus verificationStatus,
            Long userId,String certificatePath) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.achievementDate = achievementDate;
        this.verificationStatus = verificationStatus;
        this.userId = userId;
        this.certificatePath = certificatePath;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getAchievementDate() {
        return achievementDate;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public Long getUserId() {
        return userId;
    }
    public String getCertificatePath() {
        return certificatePath;
    }
}
