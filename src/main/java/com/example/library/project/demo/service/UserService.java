package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.*;
import com.example.library.project.demo.entity.DTO.LoanHistoryDTO;
import com.example.library.project.demo.entity.DTO.UserProfileDTO;
import com.example.library.project.demo.exception.BookException;
import com.example.library.project.demo.exception.UserException;
import com.example.library.project.demo.repository.LoanRepository;
import com.example.library.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import com.example.library.project.demo.security.PasswordEncoderConfig;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanService loanService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, LoanService loanService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loanService = loanService;
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

    public Integer getAccumulatedCredit(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.create("User not found"));
        return user.getCredit();
    }

    public Integer getActiveOverdueCredit(Integer userId) {
        LocalDate currentDate = LocalDate.now();
        List<LoanHistoryDTO> allLoansOfUser = loanService.getCurrentlyBorrowedBooks(userId);
        int activeOverdueCredit = allLoansOfUser.stream()
                .mapToInt(loan -> {
                    LocalDate dueDate = loan.getLoanDate().plusDays(30);
                    if (!currentDate.isAfter(dueDate)) {
                        return 0;
                    }
                    int daysOverdue = (int)  Math.ceil(ChronoUnit.DAYS.between(dueDate, currentDate));
                    return daysOverdue/2;
                })
                .sum();

        return activeOverdueCredit;
    }

    public Integer getTotalCredit(Integer userId) {
        return getAccumulatedCredit(userId) + getActiveOverdueCredit(userId);
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