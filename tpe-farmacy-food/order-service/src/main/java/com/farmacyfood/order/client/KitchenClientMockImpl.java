package com.farmacyfood.order.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile("dev")
public class KitchenClientMockImpl implements KitchenClient {

    private static final Map<Long, List<CatalogoLocalResponseDTO>> MOCK_DATA = Map.of(
            1L, List.of(
                    new CatalogoLocalResponseDTO(1L, 101L, "Brownie de Chocolate", 1L),
                    new CatalogoLocalResponseDTO(2L, 102L, "Cheesecake", 1L),
                    new CatalogoLocalResponseDTO(3L, 103L, "Tiramisú", 1L)
            ),
            2L, List.of(
                    new CatalogoLocalResponseDTO(4L, 201L, "Tostada de Palta Sin Gluten", 2L),
                    new CatalogoLocalResponseDTO(5L, 202L, "Bowl de Quinoa Sin Gluten", 2L),
                    new CatalogoLocalResponseDTO(6L, 203L, "Rolls de Primavera de Arroz", 2L)
            ),
            3L, List.of(
                    new CatalogoLocalResponseDTO(7L, 301L, "Buddha Bowl Vegano", 3L),
                    new CatalogoLocalResponseDTO(8L, 302L, "Salteado de Tofu", 3L),
                    new CatalogoLocalResponseDTO(9L, 303L, "Curry de Garbanzos", 3L)
            )
    );

    @Override
    public List<CatalogoLocalResponseDTO> getProductsByCocina(Long cocinaId) {
        return MOCK_DATA.getOrDefault(cocinaId, List.of());
    }
}