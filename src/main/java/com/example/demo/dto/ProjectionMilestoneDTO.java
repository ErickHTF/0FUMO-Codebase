package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectionMilestoneDTO {

    /** Número de dias sem fumar */
    private int days;

    /** Valor economizado acumulado até esse marco (R$) */
    private double amount;
}
