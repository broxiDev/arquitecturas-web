package com.farmacyfood.fridge.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "eventos_estado_heladera")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoEstadoHeladera {

    @Id
    private String id;

    private Long heladeraId;

    private String oldStatus;

    private String newStatus;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
