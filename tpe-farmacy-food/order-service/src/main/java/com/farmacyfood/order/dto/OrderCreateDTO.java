package com.farmacyfood.order.dto;

import java.util.List;

public record OrderCreateDTO(Long userId,
                             Long fridgeId,
                             List<OrderItemDTO> items) {
    //Solo lo necesario para crear: quién compra, dónde retira, y qué productos. El total lo calcula el backend.
}
