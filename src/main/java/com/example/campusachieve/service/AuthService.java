
package com.example.campusachieve.service;

import com.example.campusachieve.dto.LoginRequestDTO;
import com.example.campusachieve.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);
}
