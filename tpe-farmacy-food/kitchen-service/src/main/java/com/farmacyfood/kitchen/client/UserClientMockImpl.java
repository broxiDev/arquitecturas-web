package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.UserResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class UserClientMockImpl implements UserClient {

    @Override
    public boolean existeUsuario(Long usuarioId) {
        log.info("Mock: validando usuario {} -> siempre true", usuarioId);
        return true;
    }

    @Override
    public UserResponseDTO getByAuthUsername(String authUsername) {
        log.info("Mock: resolviendo authUsername {} -> id 1", authUsername);
        return new UserResponseDTO(1L, authUsername, authUsername + "@mock.com");
    }
}
