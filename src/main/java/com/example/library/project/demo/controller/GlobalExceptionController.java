package com.example.library.project.demo.controller;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GlobalExceptionController {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleIllegalAccessException(IllegalArgumentException e) {
        Map<String, String> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("timestamp", new Date().toString());
        return new Gson().toJson(map);
    }
}
