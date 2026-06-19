package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.dto.OrdenDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;

import java.util.List;

public interface CacheService {

    RecomendacionResponseDTO obtenerRecomendacionesCacheadas(Long userId);

    void guardarRecomendacionesEnCache(Long userId, List<ProductoRecomendadoDTO> productos);

    List<OrdenDTO> obtenerHistorialCacheado(Long userId);

    void guardarHistorialEnCache(Long userId, List<OrdenDTO> ordenes);
}
