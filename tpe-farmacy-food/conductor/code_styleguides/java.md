# Java Style Guide

## Naming Conventions
- Classes: PascalCase (e.g., `ProductService`)
- Methods: camelCase (e.g., `findByCategory`)
- Variables: camelCase (e.g., `productName`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_STOCK`)
- Packages: lowercase dot-separated (e.g., `com.farmacyfood.product`)

## Code Organization
- One class per file
- Group by package: `controller`, `service`, `repository`, `entity`, `dto`, `config`
- Maximum 30 lines per method
- Maximum 300 lines per class

## Best Practices
- Use records for DTOs
- Use Optional instead of null returns
- Favor constructor injection over field injection
- Use Lombok annotations judiciously
- Always use SLF4j for logging
- Write unit tests with JUnit 5 and Mockito

## Exception Handling
- Use custom exceptions extending RuntimeException
- Handle exceptions at controller level with @ControllerAdvice
- Never swallow exceptions in catch blocks
