package com.farmacyfood.order.dto;

import java.math.BigDecimal;

public record ProductSaleDTO(Long productId,
                             String productName,
                             Integer totalVendido,
                             BigDecimal totalMonto
) {
}
