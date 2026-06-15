package com.example.demo.controller;

import com.example.demo.dto.EventRequestDTO;
import com.example.demo.dto.EventResponseDTO;
import com.example.demo.dto.EventWithSuggestionsDTO;
import com.example.demo.service.EventService;
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
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventWithSuggestionsDTO> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EventRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.register(userDetails.getUsername(), dto));
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> list(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(eventService.listByUser(userDetails.getUsername()));
    }
}
