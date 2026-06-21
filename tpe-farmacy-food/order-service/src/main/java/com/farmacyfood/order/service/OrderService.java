package com.farmacyfood.order.service;

import com.farmacyfood.order.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderCreateDTO dto);

    PaymentResponseDTO payOrder(Long id);

    OrderResponseDTO confirmPickup(Long id);

    List<OrderResponseDTO> getAll();

    OrderResponseDTO getById(Long id);

    List<OrderResponseDTO> getByUser(Long userId);

    OrderResponseDTO cancelOrder(Long id, OrderCancelDTO dto);

    List<HistoricalSaleDTO> getHistorialVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId);

    List<ProductSaleDTO> getSalesByKitchen(String cocinaId, LocalDate from, LocalDate to);



}
