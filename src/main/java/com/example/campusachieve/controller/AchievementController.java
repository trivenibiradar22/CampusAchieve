
package com.example.campusachieve.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.campusachieve.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.campusachieve.dto.AchievementRequestDTO;
import com.example.campusachieve.dto.AchievementResponseDTO;
import com.example.campusachieve.entity.Achievement;
import com.example.campusachieve.service.AchievementService;
import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

	private final AchievementService achievementService;

	public AchievementController(AchievementService achievementService) {
		this.achievementService = achievementService;
	}

	// Convert entity into response DTO
	private AchievementResponseDTO convertToDTO(Achievement achievement) {

		return new AchievementResponseDTO(achievement.getId(), achievement.getTitle(), achievement.getDescription(),
				achievement.getCategory(), achievement.getAchievementDate(), achievement.getVerificationStatus(),
				achievement.getUser().getId(), achievement.getCertificatePath());
	}

	// Add Achievement

	@PostMapping("/user/{userId}")
	public ResponseEntity<AchievementResponseDTO> addAchievement(@PathVariable Long userId,
			@RequestBody AchievementRequestDTO request, @AuthenticationPrincipal User currentUser) {

		// Check ownership
		boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

		boolean isOwner = currentUser.getId().equals(userId);

		if (!isAdmin && !isOwner) {
			throw new AccessDeniedException("You cannot add achievements for another user");
		}

		Achievement achievement = new Achievement();

		achievement.setTitle(request.getTitle());
		achievement.setDescription(request.getDescription());
		achievement.setCategory(request.getCategory());
		achievement.setAchievementDate(request.getAchievementDate());

		Achievement saved = achievementService.addAchievement(userId, achievement);

		return new ResponseEntity<>(convertToDTO(saved), HttpStatus.CREATED);
	}

	// Get All Achievements
	@GetMapping
	public ResponseEntity<List<AchievementResponseDTO>> getAllAchievements(@AuthenticationPrincipal User currentUser) {

		String role = currentUser.getRole().name();

		if (!role.equals("ADMIN") && !role.equals("FACULTY")) {
			throw new AccessDeniedException("Only faculty and admin can view all achievements");
		}

		List<AchievementResponseDTO> result = achievementService.getAllAchievements().stream().map(this::convertToDTO)
				.toList();

		return ResponseEntity.ok(result);
	}

	// Get Achievements By User
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<AchievementResponseDTO>> getByUser(@PathVariable Long userId,
			@AuthenticationPrincipal User currentUser) {

		boolean isAdminOrFaculty = currentUser.getRole().name().equals("ADMIN")
				|| currentUser.getRole().name().equals("FACULTY");

		boolean isOwner = currentUser.getId().equals(userId);

		if (!isAdminOrFaculty && !isOwner) {
			throw new AccessDeniedException("You cannot view another student's achievements");
		}

		List<AchievementResponseDTO> result = achievementService.getAchievementsByUser(userId).stream()
				.map(this::convertToDTO).toList();

		return ResponseEntity.ok(result);
	}

	// Get Achievement By ID
	@GetMapping("/{id}")
	public ResponseEntity<AchievementResponseDTO> getAchievementById(@PathVariable Long id,
			@AuthenticationPrincipal User currentUser) {

		Achievement achievement = achievementService.getAchievementById(id);

		boolean isAdminOrFaculty = currentUser.getRole().name().equals("ADMIN")
				|| currentUser.getRole().name().equals("FACULTY");

		boolean isOwner = achievement.getUser().getId().equals(currentUser.getId());

		if (!isAdminOrFaculty && !isOwner) {
			throw new AccessDeniedException("You cannot view another student's achievement");
		}

		return ResponseEntity.ok(convertToDTO(achievement));
	}

	@GetMapping("/category/{category}")
	public List<AchievementResponseDTO> getAchievementsByCategory(@PathVariable String category,
			@AuthenticationPrincipal User currentUser) {

		String role = currentUser.getRole().name();

		if (!role.equals("ADMIN") && !role.equals("FACULTY")) {
			throw new AccessDeniedException("Only faculty and admin can view achievements by category");
		}

		return achievementService.getAchievementsByCategory(category).stream().map(this::convertToDTO).toList();
	}

	// Delete Achievement
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteAchievement(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {

		achievementService.deleteAchievement(id, currentUser);

		return ResponseEntity.ok("Achievement deleted successfully!");
	}

	@PutMapping("/{id}/approve")
	public AchievementResponseDTO approveAchievement(@PathVariable Long id) {
		return convertToDTO(achievementService.approveAchievement(id));
	}

	@PutMapping("/{id}/reject")
	public AchievementResponseDTO rejectAchievement(@PathVariable Long id) {
		return convertToDTO(achievementService.rejectAchievement(id));
	}

	@GetMapping("/search")
	public List<AchievementResponseDTO> searchAchievements(@RequestParam String title,
			@AuthenticationPrincipal User currentUser) {

		String role = currentUser.getRole().name();

		if (!role.equals("ADMIN") && !role.equals("FACULTY")) {
			throw new AccessDeniedException("Only faculty and admin can search all achievements");
		}

		return achievementService.getAllAchievements().stream()
				.filter(a -> a.getTitle() != null && a.getTitle().toLowerCase().contains(title.toLowerCase()))
				.map(this::convertToDTO).toList();
	}

	@GetMapping("/user/{userId}/timeline")
	public List<AchievementResponseDTO> getAchievementTimeline(@PathVariable Long userId,
			@AuthenticationPrincipal User currentUser) {

		boolean isAdminOrFaculty = currentUser.getRole().name().equals("ADMIN")
				|| currentUser.getRole().name().equals("FACULTY");

		boolean isOwner = currentUser.getId().equals(userId);

		if (!isAdminOrFaculty && !isOwner) {
			throw new AccessDeniedException("You cannot view another student's timeline");
		}

		return achievementService.getAchievementTimeline(userId).stream().map(this::convertToDTO).toList();
	}

	@PutMapping("/{id}")
	public AchievementResponseDTO updateAchievement(@PathVariable Long id, @RequestBody AchievementRequestDTO request,
			@AuthenticationPrincipal User currentUser) {

		Achievement achievement = new Achievement();

		achievement.setTitle(request.getTitle());
		achievement.setDescription(request.getDescription());
		achievement.setCategory(request.getCategory());
		achievement.setAchievementDate(request.getAchievementDate());

		Achievement updated = achievementService.updateAchievement(id, achievement, currentUser);

		return convertToDTO(updated);
	}

	@GetMapping("/page")
	public Page<AchievementResponseDTO> getAchievementsPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @AuthenticationPrincipal User currentUser) {

		String role = currentUser.getRole().name();

		if (!role.equals("ADMIN") && !role.equals("FACULTY")) {
			throw new AccessDeniedException("Only faculty and admin can view all achievements");
		}

		return achievementService.getAchievementsPage(page, size).map(this::convertToDTO);
	}

	@PostMapping(value = "/{id}/certificate", consumes = "multipart/form-data")
	public AchievementResponseDTO uploadCertificate(@PathVariable Long id, @RequestParam("file") MultipartFile file,
			@AuthenticationPrincipal User currentUser) throws IOException {

		return convertToDTO(achievementService.uploadCertificate(id, file, currentUser));
	}
}
