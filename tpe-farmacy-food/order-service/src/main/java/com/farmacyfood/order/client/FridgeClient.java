package com.farmacyfood.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "fridge-service", path = "/api/v1/heladeras")
public interface FridgeClient {

    @GetMapping("/{heladeraId}/stock")
    List<FridgeStockDTO> getStock(@PathVariable("heladeraId") Long heladeraId);

    @PutMapping("/{heladeraId}/stock")
    void updateStock(@PathVariable("heladeraId") Long heladeraId,
                     @RequestBody FridgeStockUpdateDTO dto);

}
