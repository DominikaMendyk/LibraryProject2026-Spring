package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public User addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.addUser(user);
    }

    @DeleteMapping("/remove/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId){
        userService.deleteUser(userId);
    }

    @GetMapping("/getAll")
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    @PostMapping("/{userId}/repay-credit")
    public User repayCredit(@PathVariable Integer userId,
                            @RequestParam Integer pay) {
        return userService.repayCredit(userId, pay);
    }

    @Transactional
    @PostMapping("/{userId}/add-credit")
    public User addCredit(@PathVariable Integer userId,
                            @RequestParam Integer credit) {
        return userService.addCredit(userId, credit);
    }

    @GetMapping("who-am-i")
    public String whoAmI(Authentication authentication){
        return "Username: " + authentication.getName() +", Role: " + authentication.getAuthorities();
    }
}