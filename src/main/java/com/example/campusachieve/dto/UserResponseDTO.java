package com.example.campusachieve.dto;

import com.example.campusachieve.entity.Role;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String department;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String name, String email,
                           Role role, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
    }

    public Long getId() {
        return id;
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

    public String getDepartment() {
        return department;
    }
}
