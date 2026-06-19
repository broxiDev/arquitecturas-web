package com.farmacyfood.order.service.payment;

import com.farmacyfood.order.dto.PaymentResponseDTO;

public interface PaymentGateway {

    PaymentResponseDTO processPayment(double amount, String currency);

}
