package com.farmacyfood.kitchen.entity.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "ventas_historicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaHistorica {

    @Id
    private String id;

    private Long productId;

    private String productName;

    private Long fridgeId;

    private Integer quantity;

    private BigDecimal totalAmount;

    private LocalDate date;
}
