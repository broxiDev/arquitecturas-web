package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.ProductoDTO;

import java.util.List;

public interface ProductoClient {

    List<ProductoDTO> getProductosByCategoria(String categoria);
}
