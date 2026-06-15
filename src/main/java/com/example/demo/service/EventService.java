package com.example.demo.service;

import com.example.demo.dto.EventRequestDTO;
import com.example.demo.dto.EventResponseDTO;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.exception.AssessmentNotCompletedException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventResponseDTO register(String email, EventRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        Event event = Event.builder()
                .user(user)
                .type(dto.getType())
                .intensity(dto.getIntensity())
                .trigger(dto.getTrigger())
                .notes(dto.getNotes())
                .occurredAt(dto.getOccurredAt())
                .build();

        return EventResponseDTO.from(eventRepository.save(event));
    }

    public List<EventResponseDTO> listByUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return eventRepository.findByUserIdOrderByOccurredAtDesc(user.getId())
                .stream()
                .map(EventResponseDTO::from)
                .toList();
    }
}
