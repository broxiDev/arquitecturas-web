package com.farmacyfood.kitchen.client;

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
}
