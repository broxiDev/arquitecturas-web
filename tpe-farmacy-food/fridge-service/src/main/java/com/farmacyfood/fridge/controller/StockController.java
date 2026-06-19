package com.farmacyfood.fridge.controller;

import com.farmacyfood.fridge.dto.StockCreateDTO;
import com.farmacyfood.fridge.dto.StockResponseDTO;
import com.farmacyfood.fridge.dto.StockUpdateDTO;
import com.farmacyfood.fridge.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/heladeras/{heladeraId}/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Gestión de stock en heladeras")
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Obtener stock de una heladera")
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> getStock(@PathVariable Long heladeraId) {
        return ResponseEntity.ok(stockService.getStockByHeladera(heladeraId));
    }

    @Operation(summary = "Agregar producto al stock de una heladera")
    @PostMapping
    public ResponseEntity<StockResponseDTO> addStock(@PathVariable Long heladeraId, @Valid @RequestBody StockCreateDTO dto) {
        StockResponseDTO created = stockService.addStock(heladeraId, dto);
        return ResponseEntity.created(URI.create("/api/v1/heladeras/" + heladeraId + "/stock")).body(created);
    }

    @Operation(summary = "Actualizar cantidad de stock de un producto en una heladera")
    @PutMapping
    public ResponseEntity<StockResponseDTO> updateStock(@PathVariable Long heladeraId, @Valid @RequestBody StockUpdateDTO dto) {
        return ResponseEntity.ok(stockService.updateStock(heladeraId, dto));
    }
}
