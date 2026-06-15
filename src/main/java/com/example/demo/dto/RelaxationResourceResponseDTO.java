package com.example.demo.dto;

import com.example.demo.entity.RelaxationResource;
import lombok.Data;

@Data
public class RelaxationResourceResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String trigger;

    public static RelaxationResourceResponseDTO from(RelaxationResource resource) {
        RelaxationResourceResponseDTO dto = new RelaxationResourceResponseDTO();
        dto.setId(resource.getId());
        dto.setTitle(resource.getTitle());
        dto.setDescription(resource.getDescription());
        dto.setCategory(resource.getCategory());
        dto.setTrigger(resource.getTrigger());
        return dto;
    }
}
