package com.example.demo.exception;

public class SmokingEventNotFoundException extends RuntimeException {

    public SmokingEventNotFoundException(Long id) {
        super("Evento não encontrado: " + id);
    }
}
