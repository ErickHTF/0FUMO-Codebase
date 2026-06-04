package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequestDTO {

    @NotBlank(message = "Tipo do evento é obrigatório")
    private String type;

    @NotBlank(message = "Intensidade é obrigatória")
    private String intensity;

    @NotBlank(message = "Gatilho é obrigatório")
    private String trigger;

    private String notes;

    @NotNull(message = "Data e hora do evento são obrigatórias")
    private LocalDateTime occurredAt;
}
