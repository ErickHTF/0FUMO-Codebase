package com.example.demo.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("E-mail já utilizado: " + email);
    }
}
