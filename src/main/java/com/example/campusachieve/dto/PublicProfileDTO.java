
package com.example.campusachieve.dto;

import java.util.List;

public class PublicProfileDTO {

    private Long id;
    private String name;
    private String department;
    private List<PublicAchievementDTO> achievements;

    public PublicProfileDTO(Long id, String name,
            String department,
            List<PublicAchievementDTO> achievements) {

        this.id = id;
        this.name = name;
        this.department = department;
        this.achievements = achievements;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public List<PublicAchievementDTO> getAchievements() {
        return achievements;
    }
}
