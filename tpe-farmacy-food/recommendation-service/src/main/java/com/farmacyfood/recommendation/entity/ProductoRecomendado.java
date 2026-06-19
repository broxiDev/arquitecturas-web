package com.farmacyfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto_recomendado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRecomendado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recomendacion_id", nullable = false)
    private RecomendacionResultado recomendacion;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "dietary_category", length = 100)
    private String dietaryCategory;
}
