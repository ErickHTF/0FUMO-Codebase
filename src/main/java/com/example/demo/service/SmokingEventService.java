package com.example.demo.service;

import com.example.demo.dto.CreateSmokingEventDTO;
import com.example.demo.dto.SmokingEventResponseDTO;
import com.example.demo.dto.SmokingEventStatsDTO;
import com.example.demo.entity.EventType;
import com.example.demo.entity.SmokingEvent;
import com.example.demo.entity.User;
import com.example.demo.exception.SmokingEventNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.SmokingEventRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmokingEventService {

    private final SmokingEventRepository eventRepository;
    private final UserRepository userRepository;

    public SmokingEventResponseDTO create(String email, CreateSmokingEventDTO dto) {
        if (dto.getContext() == null || dto.getContext().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contexto é obrigatório");
        }
        if (dto.getEventType() == EventType.CRAVING && dto.getIntensity() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intensidade é obrigatória para registros de desejo");
        }

        User user = getUser(email);

        SmokingEvent event = SmokingEvent.builder()
                .user(user)
                .eventType(dto.getEventType())
                .intensity(dto.getEventType() == EventType.CRAVING ? dto.getIntensity() : null)
                .context(dto.getContext())
                .note(dto.getNote())
                .occurredAt(dto.getOccurredAt() != null ? dto.getOccurredAt() : LocalDateTime.now())
                .build();

        return SmokingEventResponseDTO.from(eventRepository.save(event));
    }

    public List<SmokingEventResponseDTO> findAll(String email, EventType type) {
        Long userId = getUser(email).getId();

        List<SmokingEvent> events = type != null
                ? eventRepository.findByUser_IdAndEventTypeOrderByRecordedAtDesc(userId, type)
                : eventRepository.findByUser_IdOrderByRecordedAtDesc(userId);

        return events.stream().map(SmokingEventResponseDTO::from).toList();
    }

    public SmokingEventStatsDTO getStats(String email) {
        Long userId = getUser(email).getId();

        long totalCravings = eventRepository.countByUser_IdAndEventType(userId, EventType.CRAVING);
        long cigarettesSmoked = eventRepository.countByUser_IdAndEventType(userId, EventType.CIGARETTE_SMOKED);

        String mostFrequentContext = eventRepository
                .findByUser_IdAndEventTypeOrderByRecordedAtDesc(userId, EventType.CRAVING)
                .stream()
                .filter(e -> e.getContext() != null)
                .collect(Collectors.groupingBy(SmokingEvent::getContext, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new SmokingEventStatsDTO(totalCravings, cigarettesSmoked, mostFrequentContext);
    }

    public void delete(String email, Long eventId) {
        Long userId = getUser(email).getId();

        SmokingEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new SmokingEventNotFoundException(eventId));

        if (!event.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        eventRepository.delete(event);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(0L));
    }
}
