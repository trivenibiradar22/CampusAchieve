
package com.example.campusachieve.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.repository.AchievementRepository;
import com.example.campusachieve.repository.UserRepository;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    public FacultyController(
            UserRepository userRepository,
            AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    @GetMapping("/students")
    public List<Map<String, Object>> getStudents() {

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() != null
                        && user.getRole().name().equalsIgnoreCase("STUDENT"))
                .map(user -> {
                    Map<String, Object> student = new LinkedHashMap<>();
                    student.put("id", user.getId());
                    student.put("name", user.getName());
                    student.put("email", user.getEmail());
                    student.put("department", user.getDepartment());
                    return student;
                })
                .toList();
    }

    @GetMapping("/achievements")
    public List<Map<String, Object>> getAllAchievements() {

        return achievementRepository
                .findAllByOrderByAchievementDateDesc()
                .stream()
                .filter(achievement -> achievement.getUser() != null)
                .map(achievement -> {
                    User user = achievement.getUser();

                    Map<String, Object> record = new LinkedHashMap<>();

                    record.put("id", achievement.getId());
                    record.put("title", achievement.getTitle());
                    record.put("description", achievement.getDescription());
                    record.put("category", achievement.getCategory());
                    record.put("achievementDate",
                            achievement.getAchievementDate());

                    record.put("userId", user.getId());
                    record.put("userName", user.getName());
                    record.put("userRole",
                            user.getRole() != null
                                    ? user.getRole().name()
                                    : "UNKNOWN");
                    record.put("department", user.getDepartment());

                    return record;
                })
                .toList();
    }
    @GetMapping("/student-achievements")
    public List<Map<String, Object>> getStudentAchievements() {
        return getAchievementsByRole("STUDENT");
    }

    @GetMapping("/faculty-achievements")
    public List<Map<String, Object>> getFacultyAchievements() {
        return getAchievementsByRole("FACULTY");
    }

    private List<Map<String, Object>> getAchievementsByRole(String role) {

        return achievementRepository
                .findAllByOrderByAchievementDateDesc()
                .stream()
                .filter(achievement ->
                        achievement.getUser() != null
                        && achievement.getUser().getRole() != null
                        && achievement.getUser().getRole().name()
                                .equalsIgnoreCase(role))
                .map(achievement -> {

                    User user = achievement.getUser();

                    Map<String, Object> record = new LinkedHashMap<>();

                    record.put("id", achievement.getId());
                    record.put("title", achievement.getTitle());
                    record.put("description", achievement.getDescription());
                    record.put("category", achievement.getCategory());
                    record.put("achievementDate", achievement.getAchievementDate());
                    record.put("userId", user.getId());
                    record.put("userName", user.getName());
                    record.put("userRole", user.getRole().name());
                    record.put("department", user.getDepartment());

                    record.put("hasCertificate",
                            achievement.getCertificatePath() != null
                            && !achievement.getCertificatePath().isBlank());

                    return record;
                })
                .toList();
    }
}
