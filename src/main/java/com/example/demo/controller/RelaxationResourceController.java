package com.example.demo.controller;

import com.example.demo.dto.RelaxationResourceResponseDTO;
import com.example.demo.service.RelaxationResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relaxation-resources")
@RequiredArgsConstructor
public class RelaxationResourceController {

    private final RelaxationResourceService relaxationResourceService;

    @GetMapping
    public ResponseEntity<List<RelaxationResourceResponseDTO>> listAll(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(relaxationResourceService.listAll(userDetails.getUsername()));
    }

    @GetMapping(params = "trigger")
    public ResponseEntity<List<RelaxationResourceResponseDTO>> getByTrigger(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String trigger
    ) {
        return ResponseEntity.ok(relaxationResourceService.getByTrigger(userDetails.getUsername(), trigger));
    }
}
