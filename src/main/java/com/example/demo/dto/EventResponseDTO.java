package com.example.demo.dto;

import com.example.demo.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponseDTO {

    private Long id;
    private String type;
    private String intensity;
    private String trigger;
    private String notes;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;

    public static EventResponseDTO from(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setType(event.getType());
        dto.setIntensity(event.getIntensity());
        dto.setTrigger(event.getTrigger());
        dto.setNotes(event.getNotes());
        dto.setOccurredAt(event.getOccurredAt());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }
}
