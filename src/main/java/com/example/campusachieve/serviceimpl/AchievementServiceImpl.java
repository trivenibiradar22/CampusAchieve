
package com.example.campusachieve.serviceimpl;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.campusachieve.entity.VerificationStatus;
import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.repository.AchievementRepository;
import com.example.campusachieve.repository.UserRepository;
import com.example.campusachieve.service.AchievementService;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.example.campusachieve.service.FileStorageService;
@Service
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    public AchievementServiceImpl(
            AchievementRepository achievementRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {

        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }
    private void checkOwnership(Achievement achievement, User currentUser) {

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        boolean isOwner = achievement.getUser().getId()
                .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException(
                    "You are not allowed to modify this achievement");
        }
    }
    @Override
    public Achievement addAchievement(Long userId, Achievement achievement) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        achievement.setUser(user);

        return achievementRepository.save(achievement);
    }

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    public List<Achievement> getAchievementsByUser(Long userId) {
        return achievementRepository.findByUserId(userId);
    }

    @Override
    public Achievement getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Achievement not found"));
    }

    @Override
    public void deleteAchievement(Long id, User currentUser) {

        Achievement existing = getAchievementById(id);

        checkOwnership(existing, currentUser);

        achievementRepository.delete(existing);
    }
    @Override
    public Achievement approveAchievement(Long id) {
        Achievement achievement = getAchievementById(id);
        achievement.setVerificationStatus(VerificationStatus.APPROVED);
        return achievementRepository.save(achievement);
    }

    @Override
    public Achievement rejectAchievement(Long id) {
        Achievement achievement = getAchievementById(id);
        achievement.setVerificationStatus(VerificationStatus.REJECTED);
        return achievementRepository.save(achievement);
    }
    @Override
    public Achievement uploadCertificate(
            Long id, MultipartFile file, User currentUser)
            throws IOException {

        Achievement achievement = getAchievementById(id);

        // Check ownership
        checkOwnership(achievement, currentUser);

        String filePath = fileStorageService.storeFile(file);

        achievement.setCertificatePath(filePath);

        return achievementRepository.save(achievement);
    }
    @Override
    public List<Achievement> getAchievementsByCategory(String category) {
        return achievementRepository.findByCategoryIgnoreCase(category);
    }
    @Override
    public List<Achievement> searchAchievementsByTitle(String title) {
        return achievementRepository.findByTitleContainingIgnoreCase(title);
    }
    @Override
    public long getTotalAchievements() {
        return achievementRepository.count();
    }

    @Override
    public long getApprovedAchievements() {
        return achievementRepository.countByVerificationStatus(
                VerificationStatus.APPROVED);
    }

    @Override
    public long getPendingAchievements() {
        return achievementRepository.countByVerificationStatus(
                VerificationStatus.PENDING);
    }

    @Override
    public long getRejectedAchievements() {
        return achievementRepository.countByVerificationStatus(
                VerificationStatus.REJECTED);
    }
    @Override
    public List<Achievement> getAchievementTimeline(Long userId) {
        return achievementRepository
                .findByUserIdOrderByAchievementDateDesc(userId);
    }
    @Override
    public Page<Achievement> getAchievementsPage(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return achievementRepository.findAll(pageable);
    }
    @Override
    public Map<String, Long> getUserStatistics(Long userId) {

        return Map.of(
            "totalAchievements",
            achievementRepository.countByUserId(userId),

            "approvedAchievements",
            achievementRepository.countByUserIdAndVerificationStatus(
                    userId, VerificationStatus.APPROVED),

            "pendingAchievements",
            achievementRepository.countByUserIdAndVerificationStatus(
                    userId, VerificationStatus.PENDING),

            "rejectedAchievements",
            achievementRepository.countByUserIdAndVerificationStatus(
                    userId, VerificationStatus.REJECTED)
        );
    }
    @Override
    public Achievement updateAchievement(
            Long id,
            Achievement updatedAchievement,
            User currentUser) {

        Achievement existing = getAchievementById(id);

        checkOwnership(existing, currentUser);

        existing.setTitle(updatedAchievement.getTitle());
        existing.setDescription(updatedAchievement.getDescription());
        existing.setCategory(updatedAchievement.getCategory());
        existing.setAchievementDate(updatedAchievement.getAchievementDate());

        existing.setVerificationStatus(VerificationStatus.PENDING);

        return achievementRepository.save(existing);
    }
	@Override
	public void deleteAchievement(Long id) {
		// TODO Auto-generated method stub

	}

}
