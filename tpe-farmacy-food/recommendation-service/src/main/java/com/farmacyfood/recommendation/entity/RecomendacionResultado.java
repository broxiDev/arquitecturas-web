package com.farmacyfood.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recomendacion_resultado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecomendacionResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "recomendacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductoRecomendado> productos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }

    public void addProducto(ProductoRecomendado producto) {
        productos.add(producto);
        producto.setRecomendacion(this);
    }

    public void clearProductos() {
        productos.clear();
    }
}
