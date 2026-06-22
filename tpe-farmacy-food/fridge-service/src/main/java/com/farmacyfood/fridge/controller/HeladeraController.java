package com.farmacyfood.fridge.controller;

import com.farmacyfood.fridge.dto.FridgeRemainderDTO;
import com.farmacyfood.fridge.dto.HeladeraCreateDTO;
import com.farmacyfood.fridge.dto.HeladeraResponseDTO;
import com.farmacyfood.fridge.dto.HeladeraUpdateDTO;
import com.farmacyfood.fridge.service.HeladeraService;
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
@RequestMapping("/api/v1/heladeras")
@RequiredArgsConstructor
@Tag(name = "Heladeras", description = "Gestión de heladeras: CRUD y búsqueda por cercanía")
public class HeladeraController {

    private final HeladeraService heladeraService;
    private final StockService stockService;

    @Operation(summary = "Listar heladeras", description = "Lista todas las heladeras con filtros opcionales por cercanía o estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de heladeras (puede ser vacía)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HeladeraResponseDTO.class),
                examples = @ExampleObject(value = "[" +
                    "{\"id\":1,\"name\":\"Heladera Palermo\",\"latitude\":-34.6037,\"longitude\":-58.3816,\"address\":\"Av. Santa Fe 1234, Palermo\",\"status\":\"ACTIVE\",\"cocinaId\":\"COCINA-DULCE\",\"lastMaintenance\":\"2026-06-01\",\"createdAt\":\"2026-06-14T08:00:00\",\"updatedAt\":\"2026-06-14T10:00:00\"}," +
                    "{\"id\":2,\"name\":\"Heladera Recoleta\",\"latitude\":-34.5875,\"longitude\":-58.3924,\"address\":\"Av. Quintana 123, Recoleta\",\"status\":\"MAINTENANCE\",\"cocinaId\":\"COCINA-DULCE\",\"lastMaintenance\":\"2026-05-15\",\"createdAt\":\"2026-06-10T09:00:00\",\"updatedAt\":\"2026-06-13T11:00:00\"}" +
                "]")))
    })
    @GetMapping
    public ResponseEntity<List<HeladeraResponseDTO>> findAll(
            @Parameter(description = "Filtrar por estado (ACTIVE, MAINTENANCE, OUT_OF_SERVICE)")
            @RequestParam(required = false) String status,

            @Parameter(description = "Latitud para búsqueda por cercanía", example = "-34.6037")
            @RequestParam(required = false) Double lat,

            @Parameter(description = "Longitud para búsqueda por cercanía", example = "-58.3816")
            @RequestParam(required = false) Double lng,

            @Parameter(description = "Radio en km para búsqueda por cercanía", example = "10")
            @RequestParam(required = false) Double radius) {

        return ResponseEntity.ok(heladeraService.findAll(status, lat, lng, radius));
    }

    @Operation(summary = "Obtener heladera por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Heladera encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HeladeraResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Heladera Palermo\",\"latitude\":-34.6037,\"longitude\":-58.3816,\"address\":\"Av. Santa Fe 1234, Palermo\",\"status\":\"ACTIVE\",\"cocinaId\":\"COCINA-DULCE\",\"lastMaintenance\":\"2026-06-01\",\"createdAt\":\"2026-06-14T08:00:00\",\"updatedAt\":\"2026-06-14T10:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Heladera no encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Not Found\",\"message\":\"No existe heladera con id: 99\",\"timestamp\":\"2026-06-14T12:00:00\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<HeladeraResponseDTO> findById(
            @Parameter(description = "ID de la heladera", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(heladeraService.findById(id));
    }

    @Operation(summary = "Registrar nueva heladera")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Heladera creada exitosamente",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HeladeraResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Heladera Palermo\",\"latitude\":-34.6037,\"longitude\":-58.3816,\"address\":\"Av. Santa Fe 1234, Palermo\",\"status\":\"ACTIVE\",\"cocinaId\":\"COCINA-DULCE\",\"lastMaintenance\":null,\"createdAt\":\"2026-06-14T08:00:00\",\"updatedAt\":\"2026-06-14T08:00:00\"}"))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Validation Error\",\"message\":\"name: no debe estar vacío; latitude: no debe ser nulo\",\"timestamp\":\"2026-06-14T12:00:00\"}")))
    })
    @PostMapping
    public ResponseEntity<HeladeraResponseDTO> create(@Valid @RequestBody HeladeraCreateDTO dto) {
        HeladeraResponseDTO created = heladeraService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/heladeras/" + created.id())).body(created);
    }

    @Operation(summary = "Actualizar heladera",
               description = "Actualiza datos de una heladera. Si cambia el estado registra el evento. " +
                   "Si se reactiva a ACTIVE desde OUT_OF_SERVICE o MAINTENANCE notifica productos disponibles. " +
                   "Si pasa a OUT_OF_SERVICE o MAINTENANCE envía alerta de cambio de estado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Heladera actualizada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = HeladeraResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Heladera Palermo\",\"latitude\":-34.6037,\"longitude\":-58.3816,\"address\":\"Av. Santa Fe 1234, Palermo\",\"status\":\"ACTIVE\",\"cocinaId\":\"COCINA-DULCE\",\"lastMaintenance\":null,\"createdAt\":\"2026-06-14T08:00:00\",\"updatedAt\":\"2026-06-14T10:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Heladera no encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Not Found\",\"message\":\"No existe heladera con id: 99\",\"timestamp\":\"2026-06-14T12:00:00\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<HeladeraResponseDTO> update(
            @Parameter(description = "ID de la heladera", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody HeladeraUpdateDTO dto) {
        return ResponseEntity.ok(heladeraService.update(id, dto));
    }

    @Operation(summary = "Obtener remanente de stock por cocina",
               description = "Retorna el stock remanente de todas las heladeras asociadas a una cocina")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de remanentes (puede ser vacía)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = FridgeRemainderDTO.class),
                examples = @ExampleObject(value = "[" +
                    "{\"fridgeId\":1,\"products\":[" +
                        "{\"productId\":101,\"productName\":\"Brownie de Chocolate\",\"quantity\":3}," +
                        "{\"productId\":102,\"productName\":\"Cheesecake de Frutilla\",\"quantity\":2}" +
                    "]}," +
                    "{\"fridgeId\":2,\"products\":[" +
                        "{\"productId\":101,\"productName\":\"Brownie de Chocolate\",\"quantity\":2}" +
                    "]}" +
                "]")))
    })
    @GetMapping("/cocina/{cocinaId}/remanente")
    public ResponseEntity<List<FridgeRemainderDTO>> getRemainderByCocina(
            @Parameter(description = "ID de la cocina fantasma", example = "COCINA-DULCE")
            @PathVariable String cocinaId) {
        return ResponseEntity.ok(stockService.getRemainderByCocinaId(cocinaId));
    }

    @Operation(summary = "Eliminar heladera")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Heladera eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Heladera no encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"error\":\"Not Found\",\"message\":\"No existe heladera con id: 99\",\"timestamp\":\"2026-06-14T12:00:00\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la heladera", example = "1")
            @PathVariable Long id) {
        heladeraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
