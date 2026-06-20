package com.farmacyfood.order.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile("dev")
public class ProductClientMockImpl implements ProductClient {

    private static final Map<String, List<ProductResponseDTO>> MOCK_DATA = Map.of(
            "COCINA-DULCE", List.of(
                    new ProductResponseDTO(101L, "Brownie de Chocolate", "DULCE", "COCINA-DULCE"),
                    new ProductResponseDTO(102L, "Cheesecake", "DULCE", "COCINA-DULCE"),
                    new ProductResponseDTO(103L, "Tiramisú", "DULCE", "COCINA-DULCE")
            ),
            "COCINA-CELIACA", List.of(
                    new ProductResponseDTO(201L, "Tostada de Palta Sin Gluten", "SIN_GLUTEN", "COCINA-CELIACA"),
                    new ProductResponseDTO(202L, "Bowl de Quinoa Sin Gluten", "SIN_GLUTEN", "COCINA-CELIACA"),
                    new ProductResponseDTO(203L, "Rolls de Primavera de Arroz", "SIN_GLUTEN", "COCINA-CELIACA")
            ),
            "COCINA-VEGANA", List.of(
                    new ProductResponseDTO(301L, "Buddha Bowl Vegano", "VEGANO", "COCINA-VEGANA"),
                    new ProductResponseDTO(302L, "Salteado de Tofu", "VEGANO", "COCINA-VEGANA"),
                    new ProductResponseDTO(303L, "Curry de Garbanzos", "VEGANO", "COCINA-VEGANA")
            )
    );

    @Override
    public List<ProductResponseDTO> getProductsByCocina(String cocinaId) {
        return MOCK_DATA.getOrDefault(cocinaId, List.of());
    }
}