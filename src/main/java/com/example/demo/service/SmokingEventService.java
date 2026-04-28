package com.example.demo.service;

import com.example.demo.dto.*;
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
import java.util.*;
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

    // ── Insight 1: Mapa de Calor ──────────────────────────────────────────────

    public List<HeatmapPointDTO> getHeatmap(String email) {
        Long userId = getUser(email).getId();

        List<SmokingEvent> cravings = eventRepository
                .findByUser_IdAndEventTypeOrderByRecordedAtDesc(userId, EventType.CRAVING);

        // Agrupa por hora + dia da semana + contexto
        Map<String, List<SmokingEvent>> grouped = cravings.stream()
                .filter(e -> e.getContext() != null)
                .collect(Collectors.groupingBy(e -> {
                        LocalDateTime dt = e.getOccurredAt() != null ? e.getOccurredAt() : e.getRecordedAt();
                        return dt.getHour() + "|" + dt.getDayOfWeek().name() + "|" + e.getContext();
                }));

        return grouped.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("\\|");
                    int hour        = Integer.parseInt(parts[0]);
                    String dayOfWeek = parts[1];
                    String context   = parts[2];
                    List<SmokingEvent> events = entry.getValue();
                    long count = events.size();
                    double avgIntensity = events.stream()
                            .filter(e -> e.getIntensity() != null)
                            .mapToInt(SmokingEvent::getIntensity)
                            .average()
                            .orElse(0.0);
                    return new HeatmapPointDTO(hour, dayOfWeek, context, count,
                            Math.round(avgIntensity * 10.0) / 10.0);
                })
                .sorted(Comparator.comparingLong(HeatmapPointDTO::getCount).reversed())
                .collect(Collectors.toList());
    }

    // ── Insight 3: Correlação Gatilhos → Recaídas ────────────────────────────

    public RelapseCorrelationDTO getRelapseCorrelation(String email) {
        Long userId = getUser(email).getId();

        List<SmokingEvent> cravings = eventRepository
                .findByUser_IdAndEventTypeOrderByRecordedAtDesc(userId, EventType.CRAVING);
        List<SmokingEvent> relapses = eventRepository
                .findByUser_IdAndEventTypeOrderByRecordedAtDesc(userId, EventType.CIGARETTE_SMOKED);

        Map<String, Double> cravingPct  = computeContextPercentages(cravings);
        Map<String, Double> relapsePct  = computeContextPercentages(relapses);

        // Contextos de alto risco: recaída proporcionalmente maior que desejo (+15 p.p.)
        List<String> highRisk = relapsePct.entrySet().stream()
                .filter(e -> e.getValue() - cravingPct.getOrDefault(e.getKey(), 0.0) >= 15.0)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new RelapseCorrelationDTO(cravingPct, relapsePct, highRisk,
                relapses.size(), cravings.size());
    }

    private Map<String, Double> computeContextPercentages(List<SmokingEvent> events) {
        List<SmokingEvent> withContext = events.stream()
                .filter(e -> e.getContext() != null)
                .collect(Collectors.toList());

        long total = withContext.size();
        if (total == 0) return Collections.emptyMap();

        return withContext.stream()
                .collect(Collectors.groupingBy(SmokingEvent::getContext, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round(e.getValue() * 1000.0 / total) / 10.0
                ));
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
