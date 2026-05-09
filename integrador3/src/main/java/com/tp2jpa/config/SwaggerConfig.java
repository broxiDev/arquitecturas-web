package com.tp2jpa.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(
        title = "TP3 - Registro de Estudiantes",
        version = "1.0",
        description = "API REST para gestión de estudiantes y carreras — Arquitecturas Web TUDAI"
))
@Configuration
public class SwaggerConfig {
}
