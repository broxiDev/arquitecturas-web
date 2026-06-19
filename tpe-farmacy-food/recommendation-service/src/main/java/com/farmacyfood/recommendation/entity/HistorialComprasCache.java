package com.farmacyfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historial_compras_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialComprasCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "historial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrdenCache> ordenes = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public void addOrden(OrdenCache orden) {
        ordenes.add(orden);
        orden.setHistorial(this);
    }

    public void clearOrdenes() {
        ordenes.clear();
    }
}
