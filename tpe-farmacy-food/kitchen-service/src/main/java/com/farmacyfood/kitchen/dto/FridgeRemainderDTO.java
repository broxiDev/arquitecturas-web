package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// DTO que representa el remanente de productos en una heladera de una cocina
@Schema(description = "Remanente de productos en una heladera")
public record FridgeRemainderDTO(
    @Schema(description = "ID de la heladera", example = "1")
    Long fridgeId,

    @Schema(description = "Lista de productos con su cantidad remanente")
    List<ProductRemainderDTO> products
) {}