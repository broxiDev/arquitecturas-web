package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${clients.user-service.url:http://localhost:8086}")
@Profile("!dev")
public interface UsuarioClientFeign extends UsuarioClient {

    @Override
    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioResponseDTO getUsuario(@PathVariable("id") Long id);
}
