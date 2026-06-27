package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Solicitud para cargar productos de un cocina en una heladera")
public record CargaHeladeraRequestDTO(
    @Schema(description = "ID de la heladera donde cargar los productos", example = "1")
    @NotNull Long heladeraId,

    @Schema(description = "ID de la cocina a la que pertenecen los productos", example = "1")
    @NotNull Long cocinaId,

    @Schema(description = "Lista de productos a cargar")
    @NotEmpty @Valid List<CargaProductoDTO> productos
) {}
