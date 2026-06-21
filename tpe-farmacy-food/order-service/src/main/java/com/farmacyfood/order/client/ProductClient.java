package com.farmacyfood.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", path = "/api/v1/productos")
@Profile("!dev")
public interface ProductClient {

    @GetMapping("/cocina/{cocinaId}")
    List<ProductResponseDTO> getProductsByCocina(@PathVariable("cocinaId") String cocinaId);
}
