package com.example.demo.dto;

import com.example.demo.entity.EventType;
import com.example.demo.entity.SmokingEvent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmokingEventResponseDTO {

    private Long id;
    private EventType eventType;
    private Integer intensity;
    private String context;
    private String note;
    private LocalDateTime occurredAt;
    private LocalDateTime recordedAt;

    public static SmokingEventResponseDTO from(SmokingEvent event) {
        SmokingEventResponseDTO dto = new SmokingEventResponseDTO();
        dto.id = event.getId();
        dto.eventType = event.getEventType();
        dto.intensity = event.getIntensity();
        dto.context = event.getContext();
        dto.note = event.getNote();
        dto.occurredAt = event.getOccurredAt();
        dto.recordedAt = event.getRecordedAt();
        return dto;
    }
}
