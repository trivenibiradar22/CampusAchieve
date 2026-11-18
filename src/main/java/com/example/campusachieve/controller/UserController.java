
package com.example.campusachieve.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.campusachieve.dto.PublicProfileDTO;
import com.example.campusachieve.dto.PublicAchievementDTO;
import com.example.campusachieve.dto.UserResponseDTO;
import com.example.campusachieve.dto.ProfileUpdateDTO;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.entity.Role;
import com.example.campusachieve.service.UserService;
import com.example.campusachieve.service.AchievementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AchievementService achievementService;

    public UserController(UserService userService,
                          AchievementService achievementService) {
        this.userService = userService;
        this.achievementService = achievementService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody User user) {

        User savedUser = userService.registerUser(user);

        UserResponseDTO response = new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getDepartment()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users = userService.getAllUsers()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getDepartment()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    // Update Own Profile
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateDTO dto,
            @AuthenticationPrincipal User currentUser) {

        // Only the owner or ADMIN can update this profile
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = currentUser.getId().equals(id);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You cannot update another user's profile.");
        }

        User updatedUser = userService.updateProfile(id, dto);

        UserResponseDTO response = new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                updatedUser.getDepartment()
        );

        return ResponseEntity.ok(response);
    }

    // Public User Profile with Achievements
    @GetMapping("/{id}/profile")
    public PublicProfileDTO getUserProfile(@PathVariable Long id) {

        User user = userService.getUserById(id);

        List<PublicAchievementDTO> achievements =
                achievementService.getAchievementsByUser(id)
                .stream()
                .filter(a -> a.getCertificatePath() != null
                        && !a.getCertificatePath().isBlank())
                .map(a -> new PublicAchievementDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getCategory(),
                        a.getAchievementDate(),
                        "/api/public/certificates/" + a.getId()
                ))
                .toList();

        return new PublicProfileDTO(
                user.getId(),
                user.getName(),
                user.getDepartment(),
                achievements
        );
    }
}
