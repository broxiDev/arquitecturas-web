package com.farmacyfood.kitchen.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${clients.user-service.url:http://localhost:8086}")
@Profile("!dev")
public interface UserClientFeign extends UserClient {

    @GetMapping("/api/v1/usuarios/{id}")
    Object getById(@PathVariable("id") Long id);

    @Override
    default boolean existeUsuario(Long usuarioId) {
        try {
            getById(usuarioId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
