package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ProgressStatsDTO {

    private long totalEvents;
    private Map<String, Long> eventsByType;
    private Map<String, Long> eventsByIntensity;
    private Map<String, Long> eventsByTrigger;
    private LocalDateTime lastEventAt;
    private long daysTracking;
}
