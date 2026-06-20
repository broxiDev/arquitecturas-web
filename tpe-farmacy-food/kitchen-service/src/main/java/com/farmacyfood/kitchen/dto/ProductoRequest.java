package com.farmacyfood.kitchen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Solicitud de registro de producto en el catálogo de la cocina fantasma")
public record ProductoRequest(
    @Schema(description = "Nombre del producto", example = "Brownie de Chocolate")
    String name,

    @Schema(description = "Descripción del producto", example = "Brownie intenso con centro de chocolate fundido")
    String description,

    @Schema(description = "Categoría dietética", example = "DULCE")
    String dietaryCategory,

    @Schema(description = "Precio del producto", example = "7500.00")
    BigDecimal price,

    @Schema(description = "URL de la imagen del producto", example = "/images/brownie-chocolate.jpg")
    String imageUrl,

    @Schema(description = "Información nutricional", example = "Calorías: 450kcal, Proteínas: 8g, Carbohidratos: 55g, Grasas: 22g")
    String nutritionalInfo,

    @Schema(description = "Temperatura de conservación en °C", example = "4.00")
    BigDecimal conservacionTemperature
) {}
