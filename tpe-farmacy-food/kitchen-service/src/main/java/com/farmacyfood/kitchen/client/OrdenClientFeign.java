package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.ProductoVentaDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "order-service", url = "${clients.order-service.url:http://localhost:8083}")
@Profile("!dev")
public interface OrdenClientFeign extends OrdenClient {

    @Override
    @GetMapping("/api/v1/ordenes/historial-ventas")
    List<VentaHistoricaResponseDTO> getVentasRecientes(
        @RequestParam("from") LocalDate desde,
        @RequestParam("to") LocalDate hasta
    );

    @Override
    @GetMapping("/api/v1/ordenes/historial-ventas/cocina/{cocinaId}")
    List<ProductoVentaDTO> getSalesByKitchen(
        @PathVariable("cocinaId") String cocinaId,
        @RequestParam("from") LocalDate from,
        @RequestParam("to") LocalDate to
    );

    @Override
    @GetMapping("/api/v1/ordenes/historial-ventas")
    List<VentaHistoricaResponseDTO> findHistorialVentas(
        @RequestParam(value = "productId", required = false) Long productId,
        @RequestParam(value = "fridgeId", required = false) Long fridgeId,
        @RequestParam(value = "from", required = false) LocalDate from,
        @RequestParam(value = "to", required = false) LocalDate to
    );
}
