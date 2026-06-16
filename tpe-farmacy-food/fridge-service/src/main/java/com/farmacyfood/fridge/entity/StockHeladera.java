package com.farmacyfood.fridge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_heladera", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"heladera_id", "product_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHeladera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heladera_id", nullable = false)
    @NotNull
    private Heladera heladera;

    @Column(name = "product_id", nullable = false)
    @NotNull
    private Long productId;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer quantity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
