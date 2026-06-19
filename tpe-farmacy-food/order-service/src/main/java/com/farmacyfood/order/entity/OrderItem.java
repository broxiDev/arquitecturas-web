package com.farmacyfood.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderItemId;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private Long productId;
    private String productName;
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
    private double unitPrice;

}
