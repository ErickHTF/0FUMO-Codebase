package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "smoking_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmokingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column
    private Integer intensity;

    @Column
    private String context;

    @Column(length = 500)
    private String note;

    // Quando o evento realmente ocorreu (informado pelo usuário, padrão = agora)
    @Column
    private LocalDateTime occurredAt;

    // Quando o registro foi criado no sistema (sempre automático)
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime recordedAt;
}
