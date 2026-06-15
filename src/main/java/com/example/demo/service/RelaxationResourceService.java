package com.example.demo.service;

import com.example.demo.dto.RelaxationResourceResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.AssessmentNotCompletedException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.RelaxationResourceRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelaxationResourceService {

    private final RelaxationResourceRepository relaxationResourceRepository;
    private final UserRepository userRepository;

    public List<RelaxationResourceResponseDTO> getByTrigger(String email, String trigger) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return relaxationResourceRepository.findByTriggerIgnoreCase(trigger)
                .stream()
                .map(RelaxationResourceResponseDTO::from)
                .toList();
    }

    public List<RelaxationResourceResponseDTO> listAll(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return relaxationResourceRepository.findAll()
                .stream()
                .map(RelaxationResourceResponseDTO::from)
                .toList();
    }
}
