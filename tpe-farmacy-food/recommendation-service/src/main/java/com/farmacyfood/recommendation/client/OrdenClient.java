package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.OrdenDTO;

import java.util.List;

public interface OrdenClient {

    List<OrdenDTO> getOrdenesByUsuario(Long userId);
}
