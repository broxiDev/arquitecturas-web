package com.farmacyfood.kitchen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kitchen Service API")
                        .version("1.0.0")
                        .description("API para gestion de la cocina: preparacion de pedidos y control de stock"))
                .tags(List.of(
                        new Tag().name("Gestion de cocina por usuario").description("Crear y buscar cocinas fantasma asociadas a usuarios"),
                        new Tag().name("Gestion de productos por cocina").description("Agregar y listar productos del catalogo de una cocina"),
                        new Tag().name("Agregar productos por heladera").description("Cargar productos de una cocina en heladeras"),
                        new Tag().name("Historial de Ventas").description("Consulta de ventas historicas de la cocina fantasma"),
                        new Tag().name("Plan Diario").description("Planificacion diaria de produccion de la cocina fantasma"),
                        new Tag().name("Kitchen Service").description("Operaciones de salud del servicio de cocina fantasma")
                ));
    }
}