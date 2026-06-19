package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "${clients.order-service.url:http://localhost:8083}")
@Profile("!dev")
public interface OrdenClientFeign extends OrdenClient {

    @Override
    @GetMapping("/api/v1/ordenes/usuario/{userId}")
    List<OrdenDTO> getOrdenesByUsuario(@PathVariable("userId") Long userId);
}
