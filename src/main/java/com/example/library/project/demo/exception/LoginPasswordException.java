package com.example.library.project.demo.exception;

public class LoginPasswordException extends RuntimeException {
    private LoginPasswordException(String message){
        super(message);
    }

    public static LoginPasswordException create(String message){
        return new LoginPasswordException(message);
    }
}
