
package com.example.campusachieve.controller;

import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AchievementService achievementService;

    public DashboardController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping("/statistics")
    public Map<String, Long> getStatistics() {

        return Map.of(
            "totalAchievements",
            achievementService.getTotalAchievements(),

            "approvedAchievements",
            achievementService.getApprovedAchievements(),

            "pendingAchievements",
            achievementService.getPendingAchievements(),

            "rejectedAchievements",
            achievementService.getRejectedAchievements()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserDashboard(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {

        boolean isAdminOrFaculty =
                currentUser.getRole().name().equals("ADMIN")
                || currentUser.getRole().name().equals("FACULTY");

        boolean isOwner = currentUser.getId().equals(userId);

        if (!isAdminOrFaculty && !isOwner) {
            throw new AccessDeniedException(
                    "You cannot view another student's dashboard");
        }

        List<Achievement> achievements =
                achievementService.getAchievementsByUser(userId);

        long total = achievements.size();

        long approved = achievements.stream()
                .filter(a -> a.getVerificationStatus().name().equals("APPROVED"))
                .count();

        long pending = achievements.stream()
                .filter(a -> a.getVerificationStatus().name().equals("PENDING"))
                .count();

        long rejected = achievements.stream()
                .filter(a -> a.getVerificationStatus().name().equals("REJECTED"))
                .count();

        Map<String, Long> statistics = Map.of(
                "totalAchievements", total,
                "approvedAchievements", approved,
                "pendingAchievements", pending,
                "rejectedAchievements", rejected
        );

        return ResponseEntity.ok(statistics);
    }
}
