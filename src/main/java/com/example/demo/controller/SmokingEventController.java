package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.EventType;
import com.example.demo.service.SmokingEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SmokingEventController {

    private final SmokingEventService eventService;

    @PostMapping
    public ResponseEntity<SmokingEventResponseDTO> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSmokingEventDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.create(userDetails.getUsername(), dto));
    }

    @GetMapping
    public ResponseEntity<List<SmokingEventResponseDTO>> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) EventType type
    ) {
        return ResponseEntity.ok(eventService.findAll(userDetails.getUsername(), type));
    }

    @GetMapping("/stats")
    public ResponseEntity<SmokingEventStatsDTO> stats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(eventService.getStats(userDetails.getUsername()));
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatmapPointDTO>> heatmap(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(eventService.getHeatmap(userDetails.getUsername(), year, month));
    }

    @GetMapping("/relapse-correlation")
    public ResponseEntity<RelapseCorrelationDTO> relapseCorrelation(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(eventService.getRelapseCorrelation(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        eventService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
