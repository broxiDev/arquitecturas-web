package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.UsuarioResponseDTO;

public interface UsuarioClient {

    UsuarioResponseDTO getUsuario(Long id);
}
