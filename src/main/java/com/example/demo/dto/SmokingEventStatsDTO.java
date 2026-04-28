package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmokingEventStatsDTO {

    private long totalCravings;
    private long cigarettesSmoked;
    private String mostFrequentContext;
}
