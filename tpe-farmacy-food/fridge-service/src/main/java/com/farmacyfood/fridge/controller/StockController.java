package com.farmacyfood.fridge.controller;

import com.farmacyfood.fridge.dto.StockCreateDTO;
import com.farmacyfood.fridge.dto.StockResponseDTO;
import com.farmacyfood.fridge.dto.StockUpdateDTO;
import com.farmacyfood.fridge.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos en stock (puede ser vacía)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = StockResponseDTO.class),
                examples = @ExampleObject(value = "[" +
                    "{\"id\":1,\"fridgeId\":1,\"productId\":101,\"productName\":\"Brownie de Chocolate\",\"quantity\":10,\"updatedAt\":\"2026-06-14T10:00:00\"}," +
                    "{\"id\":2,\"fridgeId\":1,\"productId\":102,\"productName\":\"Cheesecake de Frutilla\",\"quantity\":5,\"updatedAt\":\"2026-06-14T09:30:00\"}" +
                "]")))
    })
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> getStock(
            @Parameter(description = "ID de la heladera", example = "1")
            @PathVariable Long heladeraId) {
        return ResponseEntity.ok(stockService.getStockByHeladera(heladeraId));
    }

    @Operation(summary = "Agregar producto al stock de una heladera",
               description = "Agrega un nuevo producto al stock de la heladera. " +
                   "Si la heladera está ACTIVE, notifica a los suscriptores del producto disponible.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto agregado al stock exitosamente",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = StockResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\":1,\"fridgeId\":1,\"productId\":101,\"productName\":\"Brownie de Chocolate\",\"quantity\":20,\"updatedAt\":\"2026-06-14T10:00:00\"}"))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Validation Error\",\"message\":\"productId: no debe ser nulo; quantity: debe ser mayor o igual a 0\",\"timestamp\":\"2026-06-14T12:00:00\"}")))
    })
    @PostMapping
    public ResponseEntity<StockResponseDTO> addStock(
            @Parameter(description = "ID de la heladera", example = "1")
            @PathVariable Long heladeraId,
            @Valid @RequestBody StockCreateDTO dto) {
        StockResponseDTO created = stockService.addStock(heladeraId, dto);
        return ResponseEntity.created(URI.create("/api/v1/heladeras/" + heladeraId + "/stock")).body(created);
    }

    @Operation(summary = "Actualizar cantidad de stock de un producto en una heladera",
               description = "Actualiza la cantidad de stock de un producto. " +
                   "Si la cantidad anterior era 0 y la nueva es mayor a 0, notifica a los suscriptores del producto disponible.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = StockResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\":1,\"fridgeId\":1,\"productId\":101,\"productName\":\"Brownie de Chocolate\",\"quantity\":15,\"updatedAt\":\"2026-06-14T10:30:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Stock no encontrado para ese producto en la heladera",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Not Found\",\"message\":\"No existe stock para el producto 99 en la heladera 1\",\"timestamp\":\"2026-06-14T12:00:00\"}")))
    })
    @PutMapping
    public ResponseEntity<StockResponseDTO> updateStock(
            @Parameter(description = "ID de la heladera", example = "1")
            @PathVariable Long heladeraId,
            @Valid @RequestBody StockUpdateDTO dto) {
        return ResponseEntity.ok(stockService.updateStock(heladeraId, dto));
    }
}
