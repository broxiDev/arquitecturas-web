package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.ProductoRequest;
import com.farmacyfood.kitchen.dto.ProductoResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Profile("dev")
public class ProductoClientMockImpl implements ProductoClient {

    private static final Map<Long, String> PRODUCTOS = Map.ofEntries(
        Map.entry(101L, "Brownie de Chocolate"),
        Map.entry(102L, "Cheesecake"),
        Map.entry(103L, "Tiramisú"),
        Map.entry(201L, "Tostada de Palta Sin Gluten"),
        Map.entry(202L, "Bowl de Quinoa Sin Gluten"),
        Map.entry(203L, "Rolls de Primavera de Arroz"),
        Map.entry(301L, "Buddha Bowl Vegano"),
        Map.entry(302L, "Salteado de Tofu"),
        Map.entry(303L, "Curry de Garbanzos")
    );

    private final AtomicLong idCounter = new AtomicLong(300);

    @Override
    public String getNombreProducto(Long productId) {
        return PRODUCTOS.getOrDefault(productId, "Producto Desconocido");
    }

    @Override
    public ProductoResponse registrarProductoEnCatalogo(String cocinaId, ProductoRequest request) {
        return new ProductoResponse(
            idCounter.incrementAndGet(),
            request.name(),
            request.description(),
            request.dietaryCategory(),
            request.price(),
            request.imageUrl(),
            request.nutritionalInfo(),
            request.conservacionTemperature(),
            cocinaId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}