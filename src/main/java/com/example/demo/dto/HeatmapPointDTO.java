package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeatmapPointDTO {

    /** Hora do dia (0–23) */
    private int hour;

    /** Dia da semana em inglês maiúsculo: MONDAY, TUESDAY… */
    private String dayOfWeek;

    /** Contexto/gatilho registrado */
    private String context;

    /** Número de cravings neste slot */
    private long count;

    /** Intensidade média (1.0–5.0) */
    private double avgIntensity;
}
