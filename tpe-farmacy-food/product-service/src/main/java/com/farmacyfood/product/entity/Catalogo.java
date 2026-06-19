package com.farmacyfood.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cocina_id", nullable = false, unique = true)
    private String cocinaId;

    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
        product.setCatalogo(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setCatalogo(null);
    }
}
