
package com.example.campusachieve.service;

import java.util.List;

import com.example.campusachieve.entity.User;
import com.example.campusachieve.dto.ProfileUpdateDTO;

public interface UserService {

    User registerUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User updateProfile(Long id, ProfileUpdateDTO profileUpdateDTO);
}
