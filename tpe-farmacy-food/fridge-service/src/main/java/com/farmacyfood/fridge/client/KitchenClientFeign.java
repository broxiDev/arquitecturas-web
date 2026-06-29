package com.farmacyfood.fridge.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kitchen-service", url = "${clients.kitchen-service.url:http://localhost:8084}")
@Profile("!dev")
public interface KitchenClientFeign extends KitchenClient {

    @Override
    @GetMapping("/api/v1/cocina/{cocinaId}")
    boolean cocinaExists(@PathVariable("cocinaId") Long cocinaId);
}
