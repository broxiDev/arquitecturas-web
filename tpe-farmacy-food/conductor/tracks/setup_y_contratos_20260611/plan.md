# Plan: Setup del proyecto y contratos comunes entre microservicios

## Phase 1: Proyecto base con microservicios conectados y mockeados

- [ ] Task: Crear proyecto padre Maven multi-módulo
    - [ ] Configurar pom.xml padre con Spring Boot 3.3+ y Spring Cloud 2023.0.x
    - [ ] Crear módulo `api-gateway` (Spring Cloud Gateway)
    - [ ] Crear módulo `discovery-service` (Eureka)
    - [ ] Crear módulos: `product-service`, `fridge-service`, `order-service`, `kitchen-service`, `recommendation-service`, `user-service`, `notification-service`
    - [ ] Verificar compilación con `mvn clean compile`
- [ ] Task: Configurar Docker Compose
    - [ ] Crear `docker-compose.yml` con PostgreSQL (:5432) y MongoDB (:27017)
    - [ ] Agregar perfiles para servicios de base de datos
    - [ ] Configurar volúmenes persistentes
    - [ ] Ejecutar `docker compose up -d` y validar conectividad
- [ ] Task: Implementar API Gateway con ruteo
    - [ ] Configurar Spring Cloud Gateway con Eureka discovery
    - [ ] Agregar configuración CORS
    - [ ] Configurar rutas dinámicas vía Eureka hacia cada módulo
- [ ] Task: Crear health-checks en cada microservicio
    - [ ] Agregar endpoint `/api/v1/{service}/health` en cada módulo que devuelva `{"status": "UP", "service": "{service}"}`
    - [ ] Configurar cada microservicio para registrarse en Eureka
    - [ ] Verificar que Gateway enruta correctamente a cada health-check
- [ ] Task: Integrar OpenAPI / Swagger
    - [ ] Configurar Swagger UI centralizado en el Gateway
    - [ ] Agregar anotaciones OpenAPI a los endpoints base
- [ ] Task: Verificar integración base
    - [ ] Confirmar que Eureka UI responde en :8761 con todos los servicios registrados
    - [ ] Confirmar que Gateway responde en :8080
    - [ ] Confirmar que cada ruta `/api/v1/{service}/health` responde 200
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Proyecto base con microservicios conectados y mockeados' (Protocol in workflow.md)

## Phase 2: Seguridad JWT

- [ ] Task: Configurar autenticación y autorización
    - [ ] Implementar filtro JWT en API Gateway
    - [ ] Configurar rutas públicas y protegidas
- [ ] Task: Documentar seguridad
    - [ ] Agregar configuración de seguridad a Swagger (Bearer token)
- [ ] Task: Verificar integración
    - [ ] Validar que rutas protegidas requieran token
    - [ ] Validar que rutas públicas sean accesibles sin token
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Seguridad JWT' (Protocol in workflow.md)
