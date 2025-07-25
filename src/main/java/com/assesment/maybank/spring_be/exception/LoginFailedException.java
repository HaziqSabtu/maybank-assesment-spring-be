package com.assesment.maybank.spring_be.exception;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("Login failed");
    }
}
