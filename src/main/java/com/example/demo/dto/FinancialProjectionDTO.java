package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FinancialProjectionDTO {

    /** Valor já economizado desde quitDate (R$) */
    private double savedAmount;

    /** Cigarros não fumados desde quitDate */
    private long cigarettesAvoided;

    /** Dias sem fumar */
    private long daysSmokeFreee;

    /** Projeções de economia em marcos futuros (30, 90, 180, 365 dias) */
    private List<ProjectionMilestoneDTO> milestones;

    /** Economia diária (R$/dia) */
    private double dailySaving;
}
