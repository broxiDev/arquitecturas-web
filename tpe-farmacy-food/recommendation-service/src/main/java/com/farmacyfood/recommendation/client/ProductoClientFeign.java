package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", url = "${clients.product-service.url:http://localhost:8081}")
@Profile("!dev")
public interface ProductoClientFeign extends ProductoClient {

    @Override
    @GetMapping("/api/v1/productos")
    List<ProductoDTO> getProductosByCategoria(@RequestParam("category") String categoria);
}
