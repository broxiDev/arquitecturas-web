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

    private static final Map<Long, String> PRODUCTOS = Map.of(
        101L, "Ensalada César",
        102L, "Bowl Proteico",
        103L, "Wrap de Pollo"
    );

    private final AtomicLong idCounter = new AtomicLong(200);

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
