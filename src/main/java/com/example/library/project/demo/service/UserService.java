package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        // Set initial credit to 0 if not provided
        if (user.getCredit() == null) {
            user.setCredit(0);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Integer userId){
        userRepository.deleteById(userId);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public User repayCredit(Integer userId, Integer amountPaid) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int payment = (-1) * Math.max(0,amountPaid != null ? amountPaid : 0);
        user.updateCredit(payment);
        return userRepository.save(user);
    }

    public User addCredit(Integer userId, Integer creditToAdd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.updateCredit(creditToAdd);
        return userRepository.save(user);
    }
}