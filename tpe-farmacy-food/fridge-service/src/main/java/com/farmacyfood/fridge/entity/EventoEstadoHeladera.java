package com.farmacyfood.fridge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento_estado_heladera")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoEstadoHeladera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heladera_id", nullable = false)
    @NotNull
    private Heladera heladera;

    @Column(name = "old_status", nullable = false, length = 20)
    @NotBlank
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 20)
    @NotBlank
    private String newStatus;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
