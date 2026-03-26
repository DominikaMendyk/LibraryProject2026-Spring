package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.DTO.LoginDTO;
import com.example.library.project.demo.exception.LoginPasswordException;
import com.example.library.project.demo.security.JwtTokenService;
import com.example.library.project.demo.service.LoginService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final LoginService loginService;

    @Autowired
    public LoginController(PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService, LoginService loginService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.loginService = loginService;

    }

    /*@PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginPOJO){
        //simulating getting user from database by username
        String passwordTest = "1234"; //ONLY FOR TEST
        String hashedPassword = passwordEncoder.encode(passwordTest);
        String roleFromDatabase = "ROLE_USER"; //ALSO FROM DATABASE
        boolean matches = passwordEncoder.matches(loginPOJO.getPassword(), hashedPassword);
        if(!matches){
            return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
        } else{
            String token = jwtTokenService.generateToken(loginPOJO.getUsername(), roleFromDatabase);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
    }*/
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO){
        String token = loginService.login(loginDTO.getUsername(), loginDTO.getPassword());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return new ResponseEntity<>(new Gson().toJson(map), HttpStatus.OK);
    }

    @ExceptionHandler(LoginPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String resolveLoginPasswordException(LoginPasswordException e){
        return new Gson().toJson(e.getMessage());
    }

    //ONLY FOR TESTING
    @PostMapping("/test")
    public String test(){
        return "test";
    }


}
