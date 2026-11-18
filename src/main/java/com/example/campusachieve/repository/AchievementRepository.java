
package com.example.campusachieve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.entity.VerificationStatus;

@Repository
public interface AchievementRepository
        extends JpaRepository<Achievement, Long> {

    // Get all achievements of a particular user
    List<Achievement> findByUserId(Long userId);

    // Get achievements by category
    List<Achievement> findByCategory(String category);

    // Get achievements by verification status
    List<Achievement> findByVerificationStatus(
            com.example.campusachieve.entity.VerificationStatus status);

    List<Achievement> findByCategoryIgnoreCase(String category);
    List<Achievement> findByTitleContainingIgnoreCase(String title);
    long countByVerificationStatus(VerificationStatus status);

    long countByUserId(Long userId);

    long countByUserIdAndVerificationStatus(
            Long userId, VerificationStatus status);
    List<Achievement> findByUserIdAndVerificationStatus(
            Long userId, VerificationStatus status);
    List<Achievement> findByUserIdOrderByAchievementDateDesc(Long userId);
    @EntityGraph(attributePaths = "user")
    List<Achievement> findAllByOrderByAchievementDateDesc();
}
