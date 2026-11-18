
package com.example.campusachieve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ProfileUpdateDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String department;

    private String profileImage;

    public String getName() {
        return name;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public void setName(String name) {
		this.name = name;
	}


}
