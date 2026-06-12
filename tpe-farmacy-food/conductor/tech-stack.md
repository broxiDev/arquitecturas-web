# FarmacyFood — Stack Tecnológico

## Lenguaje de Programación
- **Java 21** — Versión LTS moderna con features como records, pattern matching y virtual threads.

## Frameworks Backend
- **Spring Boot 3.3+** — Framework principal para microservicios con autoconfiguración.
- **Spring Cloud Gateway** — API Gateway para enrutamiento, filtros y balanceo de carga.
- **Spring Cloud OpenFeign** — Comunicación entre microservicios vía HTTP.
- **Spring Data JPA** — Persistencia con repositorios sobre PostgreSQL y MongoDB.

## Bases de Datos
- **PostgreSQL** — Base de datos relacional principal para datos estructurados (usuarios, pedidos, productos).
- **MongoDB** — Base de datos NoSQL para datos flexibles (preferencias, historial, recomendaciones).

## Infraestructura y DevOps
- **Maven** — Gestión de dependencias y build multi-módulo.
- **Docker + Docker Compose** — Contenerización de microservicios y bases de datos.
- **OpenAPI / Swagger** — Documentación interactiva de todas las APIs REST.

## Arquitectura
- Sistema de microservicios con un API Gateway central (Spring Cloud Gateway) y servicios independientes.
- Cada microservicio con su propia base de datos (Database per Service).
- Comunicación síncrona via OpenFeign entre servicios.

## Frontend
- **React 18** — Biblioteca de UI con hooks y JSX.
- **Vite 6** — Bundler y dev server rápido con HMR.
- **Tailwind CSS v3** — Framework de estilos utilitarios, mobile-first.
- **Zustand 5** — Estado global ligero con API de hooks.
