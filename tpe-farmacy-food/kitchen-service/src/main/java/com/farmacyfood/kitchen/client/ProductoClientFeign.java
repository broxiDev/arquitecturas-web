package com.farmacyfood.kitchen.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${clients.product-service.url:http://localhost:8081}")
@Profile("!dev")
public interface ProductoClientFeign extends ProductoClient {

    @Override
    @GetMapping("/api/v1/productos/{id}/nombre")
    String getNombreProducto(@PathVariable("id") Long productId);
}
