package com.farmacyfood.order.service.payment;

import com.farmacyfood.order.dto.PaymentResponseDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentGatewayMockImpl implements PaymentGateway {

    //esto es un mock, en un entorno real sería por ejemplo paypal o mp
    @Override
    public PaymentResponseDTO processPayment(double amount, String currency) {
        return new PaymentResponseDTO(
                "pay_" + UUID.randomUUID().toString(), //UUID genera unos digitos random
                "COMPLETED"                                     //es para simular un id
        );
    }

}
