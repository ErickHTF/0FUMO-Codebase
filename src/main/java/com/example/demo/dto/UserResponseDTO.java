package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private boolean assessmentCompleted;
    private Integer cigsPerDay;
    private String smokingYears;
    private String motivation;
    private String dependencyLevel;
    private LocalDateTime createdAt;

    public static UserResponseDTO from(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.assessmentCompleted = user.isAssessmentCompleted();
        dto.cigsPerDay = user.getCigsPerDay();
        dto.smokingYears = user.getSmokingYears();
        dto.motivation = user.getMotivation();
        dto.dependencyLevel = user.getDependencyLevel();
        dto.createdAt = user.getCreatedAt();
        return dto;
    }
}
