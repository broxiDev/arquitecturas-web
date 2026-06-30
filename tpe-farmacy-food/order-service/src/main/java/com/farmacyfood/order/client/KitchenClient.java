package com.farmacyfood.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "kitchen-service", path = "/api/v1/cocina")
@Profile("!dev")
public interface KitchenClient {

    @GetMapping("/{cocinaId}/productos")
    List<CatalogoLocalResponseDTO> getProductsByCocina(@PathVariable Long cocinaId);
}
