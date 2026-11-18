
package com.example.campusachieve.service;

import java.util.List;
import com.example.campusachieve.entity.User;
import java.util.Map;
import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.example.campusachieve.entity.Achievement;

public interface AchievementService {

    Achievement addAchievement(Long userId, Achievement achievement);

    List<Achievement> getAllAchievements();

    List<Achievement> getAchievementsByUser(Long userId);

    Achievement getAchievementById(Long id);

    void deleteAchievement(Long id);

    Achievement approveAchievement(Long id);

    Achievement rejectAchievement(Long id);
    Achievement uploadCertificate(
            Long id, MultipartFile file, User currentUser)
            throws IOException;
    List<Achievement> getAchievementsByCategory(String category);
    List<Achievement> searchAchievementsByTitle(String title);
    long getTotalAchievements();

    long getApprovedAchievements();

    long getPendingAchievements();

    long getRejectedAchievements();
    Map<String, Long> getUserStatistics(Long userId);
    List<Achievement> getAchievementTimeline(Long userId);
    Page<Achievement> getAchievementsPage(int page, int size);
    Achievement updateAchievement(
            Long id,
            Achievement updatedAchievement,
            User currentUser);

    void deleteAchievement(Long id, User currentUser);;
}
