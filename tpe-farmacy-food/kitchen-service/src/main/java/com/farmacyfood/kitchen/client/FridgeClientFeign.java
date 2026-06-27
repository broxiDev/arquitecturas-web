package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.CargaProductoDTO;
import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import com.farmacyfood.kitchen.dto.StockCreateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

// Implementacion Feign del cliente de fridge-service (activa cuando NO estamos en profile dev)
@FeignClient(name = "fridge-service", url = "${clients.fridge-service.url:http://localhost:8082}")
@Profile("!dev")
public interface FridgeClientFeign extends FridgeClient {

    @Override
    @GetMapping("/api/v1/heladeras/cocina/{cocinaId}/remanente")
    List<FridgeRemainderDTO> getRemainderByKitchen(@PathVariable("cocinaId") Long cocinaId);

    @Override
    default void cargarStockEnHeladera(Long heladeraId, Long cocinaId, List<CargaProductoDTO> productos) {
        for (CargaProductoDTO producto : productos) {
            StockCreateRequestDTO request = new StockCreateRequestDTO(
                    producto.productId(),
                    producto.productName(),
                    producto.quantity(),
                    cocinaId,
                    producto.price()
            );
            postStock(heladeraId, request);
        }
    }

    @PostMapping("/api/v1/heladeras/{heladeraId}/stock")
    void postStock(@PathVariable("heladeraId") Long heladeraId, @RequestBody StockCreateRequestDTO request);
}
