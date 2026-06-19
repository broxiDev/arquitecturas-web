package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.dto.PerfilPreferenciaDTO;

public interface PreferenciaService {

    PerfilPreferenciaDTO obtenerPreferencias(Long userId);
}
