
package com.example.campusachieve.serviceimpl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.campusachieve.dto.ProfileUpdateDTO;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.entity.Role;
import com.example.campusachieve.repository.UserRepository;
import com.example.campusachieve.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Always assign STUDENT role during public registration
        user.setRole(Role.STUDENT);

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    // Update User Profile
    @Override
    public User updateProfile(Long id, ProfileUpdateDTO dto) {

        User user = getUserById(id);

        String updatedEmail = dto.getEmail().trim();

        // Check whether another user already uses this email
        boolean emailExists = userRepository.findAll()
                .stream()
                .anyMatch(existingUser ->
                        !existingUser.getId().equals(id)
                        && existingUser.getEmail().equalsIgnoreCase(updatedEmail));

        if (emailExists) {
            throw new RuntimeException("Email already registered!");
        }

        user.setName(dto.getName().trim());
        user.setEmail(updatedEmail);
        user.setDepartment(dto.getDepartment());
        user.setProfileImage(dto.getProfileImage());

        // Password and role remain unchanged
        return userRepository.save(user);
    }
}
