package com.farmacyfood.order.dto;

public record OrderItemDTO(Long productId,
                           String productName,
                           Integer quantity,
                           double unitPrice) {
    //productName y unitPrice vienen del product-service (se desnormalizan en la orden).
    // El cliente los manda porque los conoce del catálogo. El backend puede validar contra product-service si quiere.
}
