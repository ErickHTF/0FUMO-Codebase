package com.example.demo.controller;

import com.example.demo.dto.CompleteAssessmentDTO;
import com.example.demo.dto.UpdateUserDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> ResponseEntity.ok(UserResponseDTO.from(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/me/assessment")
    public ResponseEntity<UserResponseDTO> completeAssessment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CompleteAssessmentDTO dto
    ) {
        return ResponseEntity.ok(userService.completeAssessment(userDetails.getUsername(), dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto
    ) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
