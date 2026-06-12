# Track: Setup del proyecto y contratos comunes entre microservicios

## Objetivo
Establecer la estructura base del proyecto FarmacyFood: proyecto multi-módulo Maven, infraestructura con Docker, API Gateway y contratos compartidos entre microservicios.

## Alcance
- Proyecto Maven multi-módulo con Spring Boot 3.3+
- Docker Compose con PostgreSQL y MongoDB
- Eureka Service Discovery
- API Gateway con Spring Cloud Gateway (:8080) y ruteo dinámico vía Eureka
- Módulos de negocio: product-service, fridge-service, order-service, kitchen-service, recommendation-service, user-service, notification-service
- Configuración de seguridad JWT (en API Gateway)
- Documentación OpenAPI centralizada

## Fuera de Alcance
- Lógica de negocio específica en cada microservicio
- Frontend o interfaz de usuario
- Despliegue en producción

## Criterios de Aceptación
- El proyecto compila correctamente con `mvn clean install`
- Docker Compose levanta todos los servicios base
- Eureka UI en :8761 muestra todos los microservicios registrados
- API Gateway responde en :8080 y enruta vía Eureka
- El filtro JWT está configurado y funcional
- Swagger UI accesible desde el Gateway
