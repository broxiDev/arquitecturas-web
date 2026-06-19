package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;

public interface RecomendacionService {

    RecomendacionResponseDTO getRecomendaciones(Long userId);
}
