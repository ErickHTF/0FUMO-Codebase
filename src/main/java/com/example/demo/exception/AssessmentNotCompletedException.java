package com.example.demo.exception;

public class AssessmentNotCompletedException extends RuntimeException {

    public AssessmentNotCompletedException() {
        super("Avaliação inicial não concluída. Conclua a avaliação para acessar as funcionalidades.");
    }
}
