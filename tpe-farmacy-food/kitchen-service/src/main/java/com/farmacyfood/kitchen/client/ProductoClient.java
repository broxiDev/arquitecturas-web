package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.ProductoRequest;
import com.farmacyfood.kitchen.dto.ProductoResponse;

public interface ProductoClient {
    String getNombreProducto(Long productId);
    ProductoResponse registrarProductoEnCatalogo(String cocinaId, ProductoRequest productoRequest);
}
