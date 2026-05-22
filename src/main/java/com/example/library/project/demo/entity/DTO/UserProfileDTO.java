package com.example.library.project.demo.entity.DTO;

public class UserProfileDTO {
    private Integer userId;
    private String username;
    private String email;
    private String name;
    private String role;

    public UserProfileDTO(Integer userId, String username, String email, String name, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
}