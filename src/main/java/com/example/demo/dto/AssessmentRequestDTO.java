package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssessmentRequestDTO {

    @NotNull(message = "Quantidade de cigarros por dia é obrigatória")
    @Min(value = 1, message = "Informe ao menos 1 cigarro por dia")
    private Integer cigsPerDay;

    @NotBlank(message = "Histórico de tabagismo é obrigatório")
    private String smokingYears;

    @NotBlank(message = "Motivação é obrigatória")
    private String motivation;

    @NotBlank(message = "Nível de dependência é obrigatório")
    private String dependencyLevel;
}