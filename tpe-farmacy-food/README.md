# FarmacyFood

Sistema backend de microservicios que conecta una cocina fantasma con heladeras inteligentes distribuidas en comercios.

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

