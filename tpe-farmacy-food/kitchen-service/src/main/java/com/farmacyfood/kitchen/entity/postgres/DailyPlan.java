package com.farmacyfood.kitchen.entity.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_plan", uniqueConstraints = {
    @UniqueConstraint(columnNames = "date")
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

    @Column(nullable = false, unique = true)
    private LocalDate date;

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
