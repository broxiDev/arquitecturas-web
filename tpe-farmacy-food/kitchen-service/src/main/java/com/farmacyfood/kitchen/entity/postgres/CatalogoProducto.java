package com.farmacyfood.kitchen.entity.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "catalogo_producto", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cocina_id", "product_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    @NotNull
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    @NotBlank
    private String productName;

    @Column(name = "cocina_id", nullable = false)
    @NotNull
    private Long cocinaId;
}
