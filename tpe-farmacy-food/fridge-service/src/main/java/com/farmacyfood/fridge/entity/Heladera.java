package com.farmacyfood.fridge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "heladera")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Heladera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotNull
    private Double latitude;

    @Column(nullable = false)
    @NotNull
    private Double longitude;

    @Column(nullable = false, length = 300)
    @NotBlank
    private String address;

    @Column(nullable = false, length = 20)
    @NotBlank
    private String status;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockHeladera> stockItems = new ArrayList<>();

    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
