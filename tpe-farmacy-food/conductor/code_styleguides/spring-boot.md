# Spring Boot Style Guide

## Project Structure
- Package-by-layer structure: `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `config/`, `exception/`
- Each microservice has its own Spring Boot application class
- Use `application.yml` over `application.properties`

## Configuration
- Externalize all environment-specific config
- Use @ConfigurationProperties for typed config
- Profile-based config: `application-dev.yml`, `application-prod.yml`

## REST API Design
- Use @RestController and @RequestMapping at class level
- Consistent response structure with ResponseEntity
- Document all endpoints with OpenAPI annotations
- Version APIs via URL path (e.g., `/api/v1/`)

## Dependency Injection
- Favor constructor-based injection
- Use final fields for injected dependencies
- Prefer Spring interfaces (ApplicationRunner, CommandLineRunner) over @PostConstruct

## Data Access
- Use Spring Data JPA repositories
- Define custom queries with @Query or QueryDSL
- Use @Transactional at service layer
- Use @EntityGraph for eager loading optimization

## Testing
- Use @SpringBootTest for integration tests
- Use @WebMvcTest for controller tests
- Use @DataJpaTest for repository tests
- Use Mockito for service layer tests
- Use Testcontainers for database integration tests
