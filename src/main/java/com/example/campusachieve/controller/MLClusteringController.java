
package com.example.campusachieve.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.entity.VerificationStatus;
import com.example.campusachieve.repository.AchievementRepository;
import com.example.campusachieve.repository.UserRepository;

@RestController
@RequestMapping("/api/ml")
public class MLClusteringController {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final RestClient restClient;

    public MLClusteringController(
            UserRepository userRepository,
            AchievementRepository achievementRepository,
            RestClient.Builder builder) {

        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;

        this.restClient = builder
                .baseUrl("http://127.0.0.1:5000")
                .build();
    }

    @PostMapping("/cluster")
    public ResponseEntity<Map<String, Object>> clusterStudents(
            @RequestParam(defaultValue = "4") int nClusters) {

        try {
            List<User> users = userRepository.findAll();

            List<Map<String, Object>> students = new ArrayList<>();

            for (User user : users) {

                if (user.getRole() == null ||
                        !user.getRole().name().equalsIgnoreCase("STUDENT")) {
                    continue;
                }

                List<Achievement> achievements =
                        achievementRepository.findByUserIdAndVerificationStatus(
                                user.getId(), VerificationStatus.APPROVED);

                List<Map<String, Object>> achievementList =
                        new ArrayList<>();

                for (Achievement achievement : achievements) {

                    Map<String, Object> achievementData =
                            new LinkedHashMap<>();

                    achievementData.put("title", achievement.getTitle());
                    achievementData.put(
                            "description", achievement.getDescription());
                    achievementData.put(
                            "category", achievement.getCategory());

                    achievementList.add(achievementData);
                }

                Map<String, Object> studentData =
                        new LinkedHashMap<>();

                studentData.put("student_id", user.getId());
                studentData.put("name", user.getName());
                studentData.put("department", user.getDepartment());
                studentData.put("achievements", achievementList);

                students.add(studentData);
            }

            if (students.size() < 2) {
                return ResponseEntity.badRequest().body(
                        Map.of("error",
                                "At least two student accounts are required."));
            }

            if (nClusters < 1 || nClusters > students.size()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error",
                                "Number of clusters must be between 1 and "
                                        + students.size()));
            }

            Map<String, Object> requestData =
                    new LinkedHashMap<>();

            requestData.put("n_clusters", nClusters);
            requestData.put("students", students);

            Map<String, Object> result = restClient.post()
                    .uri("/cluster")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestData)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            return ResponseEntity.ok(result);

        } catch (RestClientException e) {
            return ResponseEntity.status(502).body(
                    Map.of("error",
                            "Unable to connect to the Python ML service: "
                                    + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", e.getMessage()));
        }
    }
}
