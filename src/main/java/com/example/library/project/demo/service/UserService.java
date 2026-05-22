package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.DTO.UserProfileDTO;
import com.example.library.project.demo.entity.Role;
import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.exception.BookException;
import com.example.library.project.demo.exception.UserException;
import com.example.library.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import com.example.library.project.demo.security.PasswordEncoderConfig;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User addUser(User user) {
        try {
            // Set initial credit to 0 if not provided
            if (user.getCredit() == null) {
                user.setCredit(0);
            }
            return userRepository.save(user);
        } catch (Exception e){
            throw UserException.create("Failed to add user: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUser(Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.create("Cannot delete user: User not found"));
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw UserException.create("Failed to delete user: " + e.getMessage());
        }
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Iterable<User> getAllUsersByRole(Role role){
        return userRepository.findAllByRole(role);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserException.create("User not found"));
    }

    @Transactional
    public User repayCredit(Integer userId, Integer amountPaid) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.create("User not found"));
        if (amountPaid == null || amountPaid <= 0){
            throw UserException.create("Invalid payment amount");
        }
        int payment = (-1) * amountPaid;
        user.updateCredit(payment);
        return userRepository.save(user);
    }

    @Transactional
    public User addCredit(Integer userId, Integer creditToAdd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.create("User not found"));
        if (creditToAdd == null || creditToAdd <= 0){
            throw UserException.create("Invalid credit amount");
        }
        user.updateCredit(creditToAdd);
        return userRepository.save(user);
    }

    public UserProfileDTO getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("UNKNOWN");
        return new UserProfileDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                role
        );
    }

    @Transactional
    public String updateEmail(String username, String newEmail) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<User> existingUser = userRepository.findByEmail(newEmail);
        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getUserId(), user.getUserId())) {
            throw new RuntimeException("Email already in use");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
        return "Email updated successfully";
    }

    @Transactional
    public User updateUser(String userId, User updatedUser) {
        return userRepository.findById(Integer.valueOf(userId))
                .map(user -> {
                    if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank() &&
                            !updatedUser.getEmail().equals(user.getEmail())) {
                        Optional<User> existingUserByEmail = userRepository.findByEmail(updatedUser.getEmail());
                        if (existingUserByEmail.isPresent() &&
                                !Objects.equals(existingUserByEmail.get().getUserId(), user.getUserId())) {
                            throw new RuntimeException("Email already in use");
                        }
                        user.setEmail(updatedUser.getEmail());
                    }

                    if (updatedUser.getUsername() != null && !updatedUser.getUsername().isBlank() &&
                            !updatedUser.getUsername().equals(user.getUsername())) {
                        Optional<User> existingUserByUsername = userRepository.findByUsername(updatedUser.getUsername());
                        if (existingUserByUsername.isPresent() &&
                                !Objects.equals(existingUserByUsername.get().getUserId(), user.getUserId())) {
                            throw new RuntimeException("Username already in use");
                        }
                        user.setUsername(updatedUser.getUsername());
                    }

                    if (updatedUser.getName() != null) {
                        user.setName(updatedUser.getName());
                    }
                    if (updatedUser.getCredit() != null) {
                        user.setCredit(updatedUser.getCredit());
                    }
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> UserException.create("User not found, cannot be updated"));
    }
}