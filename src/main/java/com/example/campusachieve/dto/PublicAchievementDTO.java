
package com.example.campusachieve.dto;

import java.time.LocalDate;

public class PublicAchievementDTO {

    private Long id;
    private String title;
    private String category;
    private LocalDate achievementDate;
    private String certificateUrl;

    public PublicAchievementDTO(
            Long id,
            String title,
            String category,
            LocalDate achievementDate,
            String certificateUrl) {

        this.id = id;
        this.title = title;
        this.category = category;
        this.achievementDate = achievementDate;
        this.certificateUrl = certificateUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getAchievementDate() {
        return achievementDate;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }
}
