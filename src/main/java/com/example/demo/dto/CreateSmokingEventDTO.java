package com.example.demo.dto;

import com.example.demo.entity.EventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSmokingEventDTO {

    @NotNull(message = "Tipo de evento é obrigatório")
    private EventType eventType;

    @Min(value = 1, message = "Intensidade mínima é 1")
    @Max(value = 5, message = "Intensidade máxima é 5")
    private Integer intensity;

    private String context;

    private String note;

    // Quando ocorreu — se nulo, usa horário atual
    private LocalDateTime occurredAt;
}
