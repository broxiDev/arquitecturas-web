package com.farmacyfood.kitchen.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("dev")
public class ProductoClientMockImpl implements ProductoClient {

    private static final Map<Long, String> PRODUCTOS = Map.of(
        101L, "Ensalada César",
        102L, "Bowl Proteico",
        103L, "Wrap de Pollo"
    );

    @Override
    public String getNombreProducto(Long productId) {
        return PRODUCTOS.getOrDefault(productId, "Producto Desconocido");
    }
}
