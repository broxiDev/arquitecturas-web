package com.farmacyfood.order.dto;

public record PaymentResponseDTO(String paymentId,
                                 String status) {
    //Se devuelve después de pagar.
}
