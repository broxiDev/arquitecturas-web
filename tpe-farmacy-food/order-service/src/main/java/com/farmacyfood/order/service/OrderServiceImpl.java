package com.farmacyfood.order.service;

import com.farmacyfood.order.client.*;
import com.farmacyfood.order.dto.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final FridgeClient fridgeClient;
    private final PaymentGateway paymentGateway;
    private final ProductClient productClient;


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

    //Busca órdenes completadas (PAID o PICKED_UP) en un rango de fechas. Por cada orden, desnormaliza sus items
    //en una lista plana de ventas individuales con producto, heladera, cantidad, monto y fecha.
    //Acepta filtros opcionales por producto y heladera. Lo consume kitchen-service para mostrar el historial de ventas.
    @Override
    @Transactional(readOnly = true)
    public List<HistoricalSaleDTO> getHistorialVentas(LocalDate from, LocalDate to, Long productId, Long fridgeId) {
        List<Order> orders = orderRepository.findCompletedOrdersBetween(from, to, fridgeId);

        return orders.stream()
                .flatMap(order -> order.getItemList().stream().map(item ->
                        new HistoricalSaleDTO(
                                item.getProductId(),
                                item.getProductName(),
                                order.getFridgeId(),
                                item.getQuantity(),
                                BigDecimal.valueOf(item.getQuantity() * item.getUnitPrice()),
                                order.getCreatedAt().toLocalDate()
                        )
                ))
                .filter(dto -> productId == null || dto.productId().equals(productId))
                .collect(Collectors.toList());
    }

    //Consulta a product-service qué productos pertenecen a una cocina, busca órdenes completadas en el rango de fechas,
    //filtra solo los items de esa cocina, y los agrupa por producto sumando cantidades y montos totales.
    //Lo consume kitchen-service para calcular el plan diario de producción por cocina.
    @Override
    @Transactional(readOnly = true)
    public List<ProductSaleDTO> getSalesByKitchen(String cocinaId, LocalDate from, LocalDate to) {
        String normalizedCocinaId = cocinaId.toLowerCase().replace("_", "-");  // normalizar
        List<ProductResponseDTO> products = productClient.getProductsByCocina(normalizedCocinaId);
        Set<Long> productIds = products.stream().map(ProductResponseDTO::id).collect(Collectors.toSet());

        List<Order> orders = orderRepository.findCompletedOrdersBetween(from, to, null);

        return orders.stream()
                .flatMap(order -> order.getItemList().stream())
                .filter(item -> productIds.contains(item.getProductId()))
                .collect(Collectors.groupingBy(
                        OrderItem::getProductId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                items -> {
                                    OrderItem first = items.get(0);
                                    int totalQty = items.stream().mapToInt(OrderItem::getQuantity).sum();
                                    BigDecimal totalAmount = items.stream()
                                            .map(i -> BigDecimal.valueOf(i.getQuantity() * i.getUnitPrice()))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    return new ProductSaleDTO(first.getProductId(), first.getProductName(), totalQty, totalAmount);
                                }
                        )
                ))
                .values().stream().toList();
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
