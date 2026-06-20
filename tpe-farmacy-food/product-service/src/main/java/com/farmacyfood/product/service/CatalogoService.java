package com.farmacyfood.product.service;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;

import java.util.List;

public interface CatalogoService {
    ProductResponse createOrUpdateProduct(String cocinaId, ProductRequest productRequest);
    List<ProductResponse> getProductsByCocina(String cocinaId);
}
