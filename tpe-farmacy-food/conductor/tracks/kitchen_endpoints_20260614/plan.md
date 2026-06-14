# Implementation Plan: Kitchen Service Endpoints

## Phase 1: Docker Setup & Dependencies
- [ ] Task: Create `kitchen-service/docker-compose.yml`
    - [ ] Define `kitchen-postgres` container (PostgreSQL 16, DB: `kitchen_db`)
    - [ ] Define `kitchen-mongo` container (MongoDB 7, DB: `kitchen_db`)
    - [ ] Define volumes for data persistence
- [ ] Task: Update root `docker-compose.yml` as orchestrator
    - [ ] Add `include` directive pointing to `kitchen-service/docker-compose.yml`
- [ ] Task: Add Maven dependencies (`spring-boot-starter-data-mongodb`, `postgresql`, `validation`)
    - [ ] Update `pom.xml` with required dependencies
- [ ] Task: Conductor - User Manual Verification 'Phase 1' (Protocol in workflow.md)

## Phase 2: Entities & Database Config
- [ ] Task: Configure Database Sources
    - [ ] Configure PostgreSQL datasource in `application.yml`
    - [ ] Configure MongoDB connection in `application.yml`
    - [ ] Create `PostgresConfig.java` and `MongoConfig.java` if necessary
- [ ] Task: Define Entities
    - [ ] Create `DailyPlan.java` entity (PostgreSQL)
    - [ ] Create `PlanItem.java` entity (PostgreSQL)
    - [ ] Create `VentaHistorica.java` document (MongoDB)
- [ ] Task: Conductor - User Manual Verification 'Phase 2' (Protocol in workflow.md)

## Phase 3: DTOs & Repositories
- [ ] Task: Create DTOs
    - [ ] Create `PlanDiarioResponseDTO.java`
    - [ ] Create `ItemPlanDTO.java`
    - [ ] Create `VentaHistoricaResponseDTO.java`
    - [ ] Create `VentasResumenDTO.java` (optional, for future use)
    - [ ] Create `ProductoVentaDTO.java` (optional, for future use)
- [ ] Task: Implement Repositories
    - [ ] Create `PlanDiarioRepository.java` (JPA)
    - [ ] Create `ItemPlanRepository.java` (JPA)
    - [ ] Create `VentaHistoricaRepository.java` (MongoDB)
- [ ] Task: Conductor - User Manual Verification 'Phase 3' (Protocol in workflow.md)

## Phase 4: Core Implementation
- [ ] Task: Implement External Clients (OpenFeign + Mocks)
    - [ ] Create `OrdenClient.java` interface
    - [ ] Create `OrdenClientMockImpl.java` (`@Profile("dev")`)
    - [ ] Create `OrdenClientFeignImpl.java` (`@Profile("!dev")`)
    - [ ] Create `ProductoClient.java` interface
    - [ ] Create `ProductoClientMockImpl.java` (`@Profile("dev")`)
    - [ ] Create `ProductoClientFeignImpl.java` (`@Profile("!dev")`)
- [ ] Task: Implement Services
    - [ ] Create `PlanDiarioService.java` interface and `PlanDiarioServiceImpl.java`
        - Logic: Fetch history, calculate averages, save plan
    - [ ] Create `HistorialVentasService.java` interface and `HistorialVentasServiceImpl.java`
        - Logic: Query MongoDB with filters
- [ ] Task: Implement Controllers
    - [ ] Create `PlanDiarioController.java`
        - GET `/api/v1/cocina/plan-diario`
        - POST `/api/v1/cocina/plan-diario`
    - [ ] Create `HistorialVentasController.java`
        - GET `/api/v1/cocina/historial-ventas`
- [ ] Task: Conductor - User Manual Verification 'Phase 4' (Protocol in workflow.md)

## Phase 5: Testing & Documentation
- [ ] Task: Write Unit Tests
    - [ ] Test Service logic (calculation, repository calls)
    - [ ] Test Controller endpoints (MockMvc)
- [ ] Task: Integration Testing
    - [ ] Verify Feign client integration (mock or local)
- [ ] Task: API Documentation
    - [ ] Verify Swagger annotations and documentation generation
- [ ] Task: Conductor - User Manual Verification 'Phase 5' (Protocol in workflow.md)
