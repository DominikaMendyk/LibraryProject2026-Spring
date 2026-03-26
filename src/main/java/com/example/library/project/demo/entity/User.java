package com.example.library.project.demo.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @Column(unique=true)
    private String username;

    @Column(nullable = false)
    private String password; //NEEDS TO BE HASHED

    @Enumerated(EnumType.STRING)
    private Role role; //Enum Reader or Librarian

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer credit;
    //If positive, the user needs to pay for it before borrowing

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCredit() {return credit;}

    public void setCredit(Integer credit){
        this.credit = Objects.requireNonNullElse(credit,0);
    }

    public void updateCredit(Integer credit) {
        this.credit = Objects.requireNonNullElse(this.credit, 0)
                    + Objects.requireNonNullElse(credit, 0);
    }
}
