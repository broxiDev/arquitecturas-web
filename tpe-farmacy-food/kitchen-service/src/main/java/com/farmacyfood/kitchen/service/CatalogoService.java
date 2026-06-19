package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.ProductoRequest;
import com.farmacyfood.kitchen.dto.ProductoResponse;

public interface CatalogoService {
    ProductoResponse registrarProducto(String cocinaId, ProductoRequest request);
}
