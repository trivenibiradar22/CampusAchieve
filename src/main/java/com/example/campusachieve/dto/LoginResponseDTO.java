
package com.example.campusachieve.dto;

import com.example.campusachieve.entity.Role;

public class LoginResponseDTO {

    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(Long userId, String token,
                            String name, String email, Role role) {
        this.userId = userId;
        this.token = token;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
