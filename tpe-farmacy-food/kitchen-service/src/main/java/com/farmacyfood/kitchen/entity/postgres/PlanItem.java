package com.farmacyfood.kitchen.entity.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "plan_item", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"daily_plan_id", "product_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_plan_id", nullable = false)
    private DailyPlan dailyPlan;

    @Column(name = "product_id", nullable = false)
    @NotNull
    private Long productId;

    @Column(name = "product_name", nullable = false)
    @NotBlank
    private String productName;

    @Column(name = "suggested_quantity", nullable = false)
    @NotNull
    @Min(1)
    private Integer suggestedQuantity;
}
