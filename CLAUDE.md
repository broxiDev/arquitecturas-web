# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

Each sub-project is an independent Maven module. From the sub-project directory:

```bash
# Compile
mvn clean compile

# Run main class
mvn exec:java -Dexec.mainClass="com.tp1jdbc.Main"     # integrador1 / TP1
mvn exec:java -Dexec.mainClass="com.tp2jpa.Main"      # integrador2
```

Database must be running before executing any Main class (see Docker below).

## Database Setup

Each sub-project has its own `docker-compose.yml` that mounts `init.sql` for schema creation:

```bash
docker-compose up -d
```

- **integrador1**: MySQL on `localhost:3306` + MariaDB on `localhost:3307` — root / no password
- **integrador2**: MySQL on `localhost:3306` (`integrador2_db`) — root / no password

To populate with CSV data, uncomment `poblarDB()` in `Main.java` and run once.

## Architecture

This is an educational project structured as three progressive exercises:

### TP1 — Plain JDBC
Single-table JDBC example. `Conexion.java` manages the connection; `PersonaDAO.java` is the data layer.

### integrador1 — JDBC + Abstract Factory
Multi-database support (MySQL + MariaDB) via the **Abstract Factory** pattern:
- `AbstractFactory` → `MySQLDaoFactory` / `MariaDbDaoFactory` (both singletons)
- DAO layer: `Dao<T>` interface implemented per-entity per-database
- DTOs are used for query projections (e.g., top-5 clients, aggregates)
- `DataLoader` + `CsvLoader` handle bulk CSV ingestion

### integrador2 — JPA/Hibernate 6 + Repository Pattern
Uses Jakarta Persistence 3.1 + Hibernate 6.5. Schema is auto-managed via `hbm2ddl.auto=update`.

**Entity relationships:**
- `Estudiante` (PK: `dni`) ←OneToMany→ `EstudianteCarrera` ←ManyToOne→ `Carrera`
- `EstudianteCarrera` is the junction table with extra fields (`inscripcion`, `graduacion`, `antiguedad`)

**Key classes:**
- `JPAUtil` — `EntityManagerFactory` singleton; always call `JPAUtil.getEntityManagerFactory().close()` at shutdown
- Repositories (`EstudianteRepository`, `CarreraRepository`, `EstudianteCarreraRepository`) own `EntityManager` lifecycle per operation
- `persistence.xml` is at `src/main/resources/META-INF/persistence.xml`, persistence unit name: `"integrador2-mysql"`

**JPQL queries** use `@NamedQuery` on entities or inline strings in repository methods. Named parameters (`:param`) are the convention.

## Java Version

- TP1: Java 17
- integrador1 / integrador2: Java 21
