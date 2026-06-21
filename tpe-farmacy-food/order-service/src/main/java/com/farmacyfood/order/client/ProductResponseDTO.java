package com.farmacyfood.order.client;

public record ProductResponseDTO(Long id,
                                 String name,
                                 String dietaryCategory,
                                 String cocinaId) {
}
