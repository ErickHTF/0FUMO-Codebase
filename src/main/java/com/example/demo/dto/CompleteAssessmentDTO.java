package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteAssessmentDTO {

    @NotNull(message = "Cigarros por dia é obrigatório")
    @Min(value = 1, message = "Mínimo de 1 cigarro por dia")
    private Integer cigsPerDay;

    @NotBlank(message = "Custo do maço é obrigatório")
    private String packCostId;
}
