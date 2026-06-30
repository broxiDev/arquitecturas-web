package com.farmacyfood.order.client;

import java.math.BigDecimal;

public record FridgeStockUpdateDTO(Long productId, Long cocinaId, String productName, Integer quantity, BigDecimal price) {
}
