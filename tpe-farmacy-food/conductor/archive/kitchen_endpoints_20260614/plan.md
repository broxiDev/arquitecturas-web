# Implementation Plan: Kitchen Service Endpoints

## Phase 1: Docker Setup & Dependencies
- [x] Task: Create `kitchen-service/docker-compose.yml`
    - [x] Define `kitchen-postgres` container (PostgreSQL 16, DB: `kitchen_db`)
    - [x] Define `kitchen-mongo` container (MongoDB 7, DB: `kitchen_db`)
    - [x] Define volumes for data persistence
- [x] Task: Update root `docker-compose.yml` as orchestrator
    - [x] Add `include` directive pointing to `kitchen-service/docker-compose.yml`
- [x] Task: Add Maven dependencies (`spring-boot-starter-data-mongodb`, `postgresql`, `validation`)
    - [x] Update `pom.xml` with required dependencies
- [x] Task: Conductor - User Manual Verification 'Phase 1' (Protocol in workflow.md)

## Phase 2: Entities & Database Config
- [x] Task: Configure Database Sources
    - [x] Configure PostgreSQL datasource in `application.yml`
    - [x] Configure MongoDB connection in `application.yml`
    - [x] Create `PostgresConfig.java` and `MongoConfig.java` if necessary
- [x] Task: Define Entities
    - [x] Create `DailyPlan.java` entity (PostgreSQL)
    - [x] Create `PlanItem.java` entity (PostgreSQL)
    - [x] Create `VentaHistorica.java` document (MongoDB)
- [x] Task: Conductor - User Manual Verification 'Phase 2' (Protocol in workflow.md)

## Phase 3: DTOs & Repositories
- [x] Task: Create DTOs
    - [x] Create `PlanDiarioResponseDTO.java`
    - [x] Create `ItemPlanDTO.java`
    - [x] Create `VentaHistoricaResponseDTO.java`
    - [x] Create `VentasResumenDTO.java` (optional, for future use)
    - [x] Create `ProductoVentaDTO.java` (optional, for future use)
- [x] Task: Implement Repositories
    - [x] Create `PlanDiarioRepository.java` (JPA)
    - [x] Create `ItemPlanRepository.java` (JPA)
    - [x] Create `VentaHistoricaRepository.java` (MongoDB)
- [x] Task: Conductor - User Manual Verification 'Phase 3' (Protocol in workflow.md)

## Phase 4: Core Implementation
- [x] Task: Implement External Clients (OpenFeign + Mocks)
    - [x] Create `OrdenClient.java` interface
    - [x] Create `OrdenClientMockImpl.java` (`@Profile("dev")`)
    - [x] Create `OrdenClientFeignImpl.java` (`@Profile("!dev")`)
    - [x] Create `ProductoClient.java` interface
    - [x] Create `ProductoClientMockImpl.java` (`@Profile("dev")`)
    - [x] Create `ProductoClientFeignImpl.java` (`@Profile("!dev")`)
- [x] Task: Implement Services
    - [x] Create `PlanDiarioService.java` interface and `PlanDiarioServiceImpl.java`
        - Logic: Fetch history, calculate averages, save plan
    - [x] Create `HistorialVentasService.java` interface and `HistorialVentasServiceImpl.java`
        - Logic: Query MongoDB with filters
- [x] Task: Implement Controllers
    - [x] Create `PlanDiarioController.java`
        - GET `/api/v1/cocina/plan-diario`
        - POST `/api/v1/cocina/plan-diario`
    - [x] Create `HistorialVentasController.java`
        - GET `/api/v1/cocina/historial-ventas`
- [x] Task: Conductor - User Manual Verification 'Phase 4' (Protocol in workflow.md)

## Phase 5: Testing & Documentation
- [x] Task: Write Unit Tests
    - [x] Test Service logic (calculation, repository calls)
    - [x] Test Controller endpoints (MockMvc)
- [x] Task: Integration Testing
    - [x] Verify Feign client integration (mock or local)
- [x] Task: API Documentation
    - [x] Verify Swagger annotations and documentation generation
- [x] Task: Conductor - User Manual Verification 'Phase 5' (Protocol in workflow.md)
