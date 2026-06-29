package com.farmacyfood.fridge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_heladera", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"heladera_id", "cocina_id", "product_id"})
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

    @Column(name = "cocina_id", nullable = false)
    @NotNull
    private Long cocinaId;

    @Column(name = "product_id", nullable = false)
    @NotNull
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    @NotNull
    private String productName;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    private BigDecimal price;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
