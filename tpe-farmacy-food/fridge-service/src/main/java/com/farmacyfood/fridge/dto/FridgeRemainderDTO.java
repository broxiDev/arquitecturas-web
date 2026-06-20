package com.farmacyfood.fridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Remanente de productos en una heladera de una cocina")
public record FridgeRemainderDTO(
    @Schema(description = "ID de la heladera", example = "1")
    Long fridgeId,

    @Schema(description = "Lista de productos con su cantidad remanente")
    List<ProductRemainderDTO> products
) {}
