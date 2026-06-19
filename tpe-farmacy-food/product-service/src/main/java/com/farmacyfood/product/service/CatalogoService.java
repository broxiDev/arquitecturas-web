package com.farmacyfood.product.service;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;

public interface CatalogoService {
    ProductResponse createOrUpdateProduct(String cocinaId, ProductRequest productRequest);
}
