package com.farmacyfood.order.service;

import com.farmacyfood.order.client.FridgeClient;
import com.farmacyfood.order.client.FridgeStockDTO;
import com.farmacyfood.order.client.FridgeStockUpdateDTO;
import com.farmacyfood.order.client.UserClient;
import com.farmacyfood.order.dto.OrderCancelDTO;
import com.farmacyfood.order.dto.OrderCreateDTO;
import com.farmacyfood.order.dto.OrderItemDTO;
import com.farmacyfood.order.dto.OrderResponseDTO;
import com.farmacyfood.order.dto.PaymentResponseDTO;
import com.farmacyfood.order.entity.Order;
import com.farmacyfood.order.entity.OrderItem;
import com.farmacyfood.order.exception.FailedPaymentException;
import com.farmacyfood.order.exception.OrderNotFoundException;
import com.farmacyfood.order.exception.OutOfStockException;
import com.farmacyfood.order.repository.OrderRepository;
import com.farmacyfood.order.service.payment.PaymentGateway;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final FridgeClient fridgeClient;
    private final PaymentGateway paymentGateway;


    //  Valida usuario con UserClient.getUser(userId) — si 404, lanza excepción
    //  Verifica stock con FridgeClient.getStock(fridgeId, productId) por cada item
    //	 Guarda con orderRepository.save(order) — cascade guarda OrderItems
    //	 Mapea Order → OrderResponseDTO y retorna
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        try {
            userClient.getUser(dto.userId());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new OrderNotFoundException("Usuario no encontrado: " + dto.userId());
            }
            throw e;
        }

        List<FridgeStockDTO> stock = fridgeClient.getStock(dto.fridgeId());

        for (OrderItemDTO item : dto.items()) {
            FridgeStockDTO stockItem = stock.stream()
                    .filter(s -> s.productId().equals(item.productId()))
                    .findFirst()
                    .orElseThrow(() -> new OutOfStockException(
                            "Producto " + item.productId() + " no encontrado en la heladera"));

            if (stockItem.quantity() < item.quantity()) {
                throw new OutOfStockException(
                        "Stock insuficiente para producto " + item.productId()
                                + ". Disponible: " + stockItem.quantity()
                                + ", solicitado: " + item.quantity());
            }
        }

        Order order = toEntity(dto);
        order = orderRepository.save(order);
        return toResponseDTO(order);
    }

    //  busca si existe la orden en orderRepository.findById(id) o lanza OrderNotFoundException
    //	Valida status == PENDING o lanza IllegalStateException
    //	PaymentGateway.processPayment() → mock retorna éxito con paymentId
    //	Status → PAID, guarda paymentId
    //	FridgeClient.reserveStock(fridgeId, items) — reserva stock
    //	Guarda orden y retorna PaymentResponseDTO
    @Override
    @Transactional
    public PaymentResponseDTO payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));

        if (!"PENDING".equals(order.getStatus())) {
            throw new FailedPaymentException("La orden no está en estado PENDING");
        }

        PaymentResponseDTO payment = paymentGateway.processPayment(order.getTotal(), "USD");

        if (!"COMPLETED".equals(payment.status())) {
            throw new FailedPaymentException("El pago no fue completado");
        }

        order.setStatus("PAID");
        order.setPaymentId(payment.paymentId());
        orderRepository.save(order);

        List<FridgeStockDTO> stock = fridgeClient.getStock(order.getFridgeId());

        for (OrderItem item : order.getItemList()) {
            FridgeStockDTO stockItem = stock.stream()
                    .filter(s -> s.productId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new OutOfStockException(
                            "Producto no encontrado en stock: " + item.getProductId()));

            int newQty = stockItem.quantity() - item.getQuantity();
            fridgeClient.updateStock(order.getFridgeId(),
                    new FridgeStockUpdateDTO(item.getProductId(), newQty));
        }

        return payment;
    }

    // busca la orden con FindById o lanza OrderNotFoundException
    //	Valida que status == PAID
    //	cambia el status pending a Status → PICKED_UP
    //	FridgeClient.decrementStock(fridgeId, items) — libera stock definitivo
    //	Guarda y retorna OrderResponseDTO
    @Override
    @Transactional
    public OrderResponseDTO confirmPickup(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));

        if (!"PAID".equals(order.getStatus())) {
            throw new IllegalStateException("La orden no está en estado PAID");
        }

        order.setStatus("PICKED_UP");
        orderRepository.save(order);

        return toResponseDTO(order);
    }

    //obtiene todas las ordenes
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    //obtiene una orden por id
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getById(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));
    }

    //obtiene todas las ordenes de determinado usuario
    //Spring Data JPA genera automáticamente:
    //SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    //cancela orden
    // busca orden con FindById o throw
    //Valida que status sea PENDING o PAID
    //Si estaba PAID: FridgeClient.releaseStock(fridgeId, items) — libera reserva
    //Status → CANCELLED, guarda y retorna
    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Long id, OrderCancelDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));

        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new IllegalStateException("La orden no está en estado PENDING o PAID");
        }

        if ("PAID".equals(order.getStatus())) {
            List<FridgeStockDTO> stock = fridgeClient.getStock(order.getFridgeId());

            for (OrderItem item : order.getItemList()) {
                FridgeStockDTO stockItem = stock.stream()
                        .filter(s -> s.productId().equals(item.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new OutOfStockException(
                                "Producto no encontrado en stock: " + item.getProductId()));

                int newQty = stockItem.quantity() + item.getQuantity();
                fridgeClient.updateStock(order.getFridgeId(),
                        new FridgeStockUpdateDTO(item.getProductId(), newQty));
            }
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return toResponseDTO(order);
    }

    //métodos para conversión de entidades a dtos y viceversa

    //de entidad a dto
    private OrderResponseDTO toResponseDTO(Order order) {
        List<OrderItemDTO> items = order.getItemList().stream()
                .map(item -> new OrderItemDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        return new OrderResponseDTO(
                order.getOrderId(),
                order.getUserId(),
                order.getFridgeId(),
                items,
                order.getTotal(),
                order.getStatus(),
                order.getPaymentId(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    //de dto a entidad y crea la orden asignando los datos
    //	Mapea OrderCreateDTO → Order entity (status = PENDING)
    //	Calcula total = sumatoria de (quantity × unitPrice)
    private Order toEntity(OrderCreateDTO dto) {
        Order order = new Order();
        order.setUserId(dto.userId());
        order.setFridgeId(dto.fridgeId());
        order.setStatus("PENDING");

        List<OrderItem> items = dto.items().stream()
                .map(itemDto -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(itemDto.productId());
                    item.setProductName(itemDto.productName());
                    item.setQuantity(itemDto.quantity());
                    item.setUnitPrice(itemDto.unitPrice());
                    item.setOrder(order);
                    return item;
                })
                .toList();

        order.setItemList(items);

        double total = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getUnitPrice())
                .sum();
        order.setTotal(total);

        return order;
    }

}
