package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateQuitDateDTO {

    @NotBlank(message = "Data é obrigatória")
    private String quitDate; // formato: yyyy-MM-dd
}
