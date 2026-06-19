package com.farmacyfood.order.service;

import com.farmacyfood.order.dto.OrderCancelDTO;
import com.farmacyfood.order.dto.OrderCreateDTO;
import com.farmacyfood.order.dto.OrderResponseDTO;
import com.farmacyfood.order.dto.PaymentResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderCreateDTO dto);

    PaymentResponseDTO payOrder(Long id);

    OrderResponseDTO confirmPickup(Long id);

    List<OrderResponseDTO> getAll();

    OrderResponseDTO getById(Long id);

    List<OrderResponseDTO> getByUser(Long userId);

    OrderResponseDTO cancelOrder(Long id, OrderCancelDTO dto);

}
