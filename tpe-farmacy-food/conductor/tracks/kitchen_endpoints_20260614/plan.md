# Implementation Plan: Kitchen Service Endpoints

## Phase 1: Setup & Entities
- [ ] Task: Add Maven dependencies (`spring-boot-starter-data-mongodb`, `postgresql`, `validation`)
    - [ ] Update `pom.xml` with required dependencies
- [ ] Task: Configure Database Sources
    - [ ] Configure PostgreSQL datasource in `application.yml`
    - [ ] Configure MongoDB connection in `application.yml`
    - [ ] Create `PostgresConfig.java` and `MongoConfig.java` if necessary
- [ ] Task: Define Entities
    - [ ] Create `DailyPlan.java` entity (PostgreSQL)
    - [ ] Create `PlanItem.java` entity (PostgreSQL)
    - [ ] Create `VentaHistorica.java` document (MongoDB)
- [ ] Task: Conductor - User Manual Verification 'Phase 1' (Protocol in workflow.md)

## Phase 2: DTOs & Repositories
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
- [ ] Task: Conductor - User Manual Verification 'Phase 2' (Protocol in workflow.md)

## Phase 3: Core Implementation
- [ ] Task: Implement OpenFeign Client
    - [ ] Create `OrdenClient.java` to fetch sales history from `order-service`
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
- [ ] Task: Conductor - User Manual Verification 'Phase 3' (Protocol in workflow.md)

## Phase 4: Testing & Documentation
- [ ] Task: Write Unit Tests
    - [ ] Test Service logic (calculation, repository calls)
    - [ ] Test Controller endpoints (MockMvc)
- [ ] Task: Integration Testing
    - [ ] Verify Feign client integration (mock or local)
- [ ] Task: API Documentation
    - [ ] Verify Swagger annotations and documentation generation
- [ ] Task: Conductor - User Manual Verification 'Phase 4' (Protocol in workflow.md)
