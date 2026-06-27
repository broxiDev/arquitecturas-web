package com.farmacyfood.kitchen.entity.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa el plan diario de produccion de una cocina fantasma
@Entity
@Table(name = "daily_plan", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"date", "cocina_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    // Identificador de la cocina a la que pertenece este plan
    @Column(name = "cocina_id", nullable = false)
    @NotNull
    private Long cocinaId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "dailyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlanItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(PlanItem item) {
        items.add(item);
        item.setDailyPlan(this);
    }

    public void clearItems() {
        items.forEach(item -> item.setDailyPlan(null));
        items.clear();
    }
}
