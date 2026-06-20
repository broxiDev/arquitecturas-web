package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Implementación Feign del cliente de fridge-service (activa cuando NO estamos en profile dev)
@FeignClient(name = "fridge-service", url = "${clients.fridge-service.url:http://localhost:8082}")
@Profile("!dev")
public interface FridgeClientFeign extends FridgeClient {

    @Override
    @GetMapping("/api/v1/heladeras/cocina/{cocinaId}/remanente")
    List<FridgeRemainderDTO> getRemainderByKitchen(@PathVariable("cocinaId") String cocinaId);
}