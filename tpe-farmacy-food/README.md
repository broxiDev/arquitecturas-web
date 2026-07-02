# FarmacyFood

## MVP
![img.png](img.png)

Sistema backend de microservicios que conecta una cocina fantasma con heladeras inteligentes distribuidas en comercios.

## Getting Started

Ver la [Guía de Levantamiento y Pruebas](./docs/SETUP-TESTING-GUIDE.md) (docs/SETUP-TESTING-GUIDE) para instrucciones detalladas sobre:

- Levantamiento de bases de datos con Docker Compose
- Inicio de Eureka, API Gateway y los 7 microservicios
- Datos semilla precargados (productos, heladeras, usuarios, órdenes)
- Flujos de prueba para las 16 user stories con ejemplos curl
- Troubleshooting

## Swagger

### Centralizado (Gateway)
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Por microservicio

| Servicio | Puerto | Swagger |
|---|---|---|
| product-service | 8081 | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| fridge-service | 8082 | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| order-service | 8083 | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |
| kitchen-service | 8084 | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) |
| recommendation-service | 8085 | [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) |
| user-service | 8086 | [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html) |
| notification-service | 8087 | [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html) |

### Otros

| Servicio | Puerto | URL |
|---|---|---|
| Eureka | 8761 | [http://localhost:8761](http://localhost:8761) |
| API Gateway | 8080 | [http://localhost:8080](http://localhost:8080) |

## Swagger (Docker, puertos 7x)

Si levantaste todo con `docker compose up -d --build` desde la raíz, los puertos publicados al host son los mismos pero con el `8` inicial cambiado por `7`:

### Centralizado (Gateway)
- **Swagger UI:** [http://localhost:7080/swagger-ui.html](http://localhost:7080/swagger-ui.html)

### Por microservicio

| Servicio | Puerto | Swagger |
|---|---|---|
| fridge-service | 7082 | [http://localhost:7082/swagger-ui.html](http://localhost:7082/swagger-ui.html) |
| order-service | 7083 | [http://localhost:7083/swagger-ui.html](http://localhost:7083/swagger-ui.html) |
| kitchen-service | 7084 | [http://localhost:7084/swagger-ui.html](http://localhost:7084/swagger-ui.html) |
| recommendation-service | 7085 | [http://localhost:7085/swagger-ui.html](http://localhost:7085/swagger-ui.html) |
| user-service | 7086 | [http://localhost:7086/swagger-ui.html](http://localhost:7086/swagger-ui.html) |
| notification-service | 7087 | [http://localhost:7087/swagger-ui.html](http://localhost:7087/swagger-ui.html) |
| audit-service | 7088 | [http://localhost:7088/swagger-ui.html](http://localhost:7088/swagger-ui.html) |
| auth-service | 7089 | [http://localhost:7089/swagger-ui.html](http://localhost:7089/swagger-ui.html) |

### Otros

| Servicio | Puerto | URL |
|---|---|---|
| Eureka | 7761 | [http://localhost:7761](http://localhost:7761) |
| API Gateway | 7080 | [http://localhost:7080](http://localhost:7080) |

