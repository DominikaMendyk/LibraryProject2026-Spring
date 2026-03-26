package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.exception.LoginPasswordException;
import com.example.library.project.demo.repository.UserRepository;
import com.example.library.project.demo.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public String login(String username, String password) {
        Collection<User> list = userRepository.findUserByUsername(username);
        if (list.isEmpty()) {
            throw LoginPasswordException.create("Incorrect login or password");
        } else{
            User user = list.iterator().next();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtTokenService.generateToken(username, user.getRole());
            } else{
                throw LoginPasswordException.create("Incorrect login or password");
            }
        }
    }
}
