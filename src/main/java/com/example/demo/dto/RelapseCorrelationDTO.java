package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RelapseCorrelationDTO {

    /**
     * Distribuição percentual de contextos nos CRAVINGS.
     * Ex: { "Estresse": 40.0, "Social": 25.0, "Hábito": 35.0 }
     */
    private Map<String, Double> cravingContextPercentages;

    /**
     * Distribuição percentual de contextos nas RECAÍDAS (CIGARETTE_SMOKED).
     * Ex: { "Social": 90.0, "Hábito": 10.0 }
     */
    private Map<String, Double> relapseContextPercentages;

    /**
     * Contextos onde a taxa de recaída é desproporcionalmente maior que a de desejos.
     * Ordenados por risco decrescente.
     */
    private List<String> highRiskContexts;

    /** Total de recaídas registradas. */
    private long totalRelapses;

    /** Total de desejos registrados. */
    private long totalCravings;
}
