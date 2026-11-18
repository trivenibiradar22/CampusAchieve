
package com.example.campusachieve.serviceimpl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.campusachieve.dto.LoginRequestDTO;
import com.example.campusachieve.dto.LoginResponseDTO;
import com.example.campusachieve.entity.User;
import com.example.campusachieve.repository.UserRepository;
import com.example.campusachieve.security.JwtService;
import com.example.campusachieve.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null ||
            !passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(
                user.getId(),
                token,
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
