package com.example.demo.repository;

import com.example.demo.entity.EventType;
import com.example.demo.entity.SmokingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmokingEventRepository extends JpaRepository<SmokingEvent, Long> {

    List<SmokingEvent> findByUser_IdOrderByRecordedAtDesc(Long userId);

    List<SmokingEvent> findByUser_IdAndEventTypeOrderByRecordedAtDesc(Long userId, EventType eventType);

    long countByUser_IdAndEventType(Long userId, EventType eventType);
}
