package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.UserResponseDTO;

public interface UserClient {
    boolean existeUsuario(Long usuarioId);

    UserResponseDTO getByAuthUsername(String authUsername);
}
