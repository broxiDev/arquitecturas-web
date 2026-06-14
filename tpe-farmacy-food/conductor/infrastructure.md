# FarmacyFood — Infraestructura y Desarrollo

## Estrategia Docker: Database per Service

Cada microservicio tiene sus **propios contenedores de base de datos**. Esto garantiza aislamiento total entre los integrantes del equipo durante el desarrollo.

### Estructura del Proyecto

```
tpe-farmacy-food/
├── docker-compose.yml                  ← Orquestador (incluye todos los servicios)
├── kitchen-service/
│   ├── docker-compose.yml              ← kitchen-postgres, kitchen-mongo
│   ├── src/
│   └── pom.xml
├── order-service/
│   ├── docker-compose.yml              ← order-postgres
│   ├── src/
│   └── pom.xml
├── product-service/
│   ├── docker-compose.yml              ← product-postgres
│   ├── src/
│   └── pom.xml
├── fridge-service/
│   ├── docker-compose.yml              ← fridge-postgres
│   └── ...
├── user-service/
│   ├── docker-compose.yml              ← user-postgres
│   └── ...
├── recommendation-service/
│   ├── docker-compose.yml              ← recommendation-mongo
│   └── ...
└── notification-service/
    ├── docker-compose.yml              ← notification-mongo
    └── ...
```

### Convenciones de Base de Datos

- **Usuario:** `root` (todos los servicios)
- **Password:** Sin password (autenticación trust)
- **PostgreSQL:** Se requiere `POSTGRES_HOST_AUTH_METHOD: trust` en el docker-compose
- **MongoDB:** Sin autenticación por defecto (no requiere configuración extra)

### Convenciones de Nombres de Contenedores

| Servicio | Contenedor Postgres | Contenedor Mongo |
|---|---|---|
| kitchen-service | `kitchen-postgres` | `kitchen-mongo` |
| order-service | `order-postgres` | — |
| product-service | `product-postgres` | — |
| fridge-service | `fridge-postgres` | — |
| user-service | `user-postgres` | — |
| recommendation-service | — | `recommendation-mongo` |
| notification-service | — | `notification-mongo` |

**Regla:** `<nombre-servicio>-<tipo-db>` para evitar colisiones de nombres.

### Cómo Usar

#### Desarrollo Individual
Cuando trabajás en tu microservicio, solo levantás tus contenedores:

```bash
cd kitchen-service/
docker compose up -d
```

Esto levanta solo `kitchen-postgres` y `kitchen-mongo`. No afecta a nadie más.

#### Integración Completa
Cuando el equipo necesita probar todo junto:

```bash
cd tpe-farmacy-food/
docker compose up -d
```

Esto levanta **todos** los servicios y todas las bases de datos gracias al `include` en el `docker-compose.yml` raíz.

#### Resetear Solo Tu Base
Si necesitás limpiar tus datos sin afectar a otros:

```bash
cd kitchen-service/
docker compose down -v    # Elimina contenedores Y volúmenes
docker compose up -d      # Arranca limpio
```

### Archivo docker-compose.yml por Servicio (Plantilla)

Cada microservicio debe crear su propio `docker-compose.yml` con esta estructura:

```yaml
# kitchen-service/docker-compose.yml
services:
  kitchen-postgres:
    image: postgres:16
    container_name: kitchen-postgres
    environment:
      POSTGRES_DB: kitchen_db
      POSTGRES_USER: root
      POSTGRES_HOST_AUTH_METHOD: trust
    ports:
      - "5432:5432"
    volumes:
      - kitchen_postgres_data:/var/lib/postgresql/data

  kitchen-mongo:
    image: mongo:7
    container_name: kitchen-mongo
    environment:
      MONGO_INITDB_DATABASE: kitchen_db
    ports:
      - "27017:27017"
    volumes:
      - kitchen_mongo_data:/data/db

volumes:
  kitchen_postgres_data:
  kitchen_mongo_data:
```

**Nota sobre puertos:** Si trabajás en varios servicios a la vez, cambiá los puertos host para evitar conflictos (ej: `5433:5432`).

### Archivo docker-compose.yml Raíz (Orquestador)

El archivo raíz usa `include` para componer todos los servicios:

```yaml
# tpe-farmacy-food/docker-compose.yml
include:
  - path: ./kitchen-service/docker-compose.yml
  - path: ./order-service/docker-compose.yml
  - path: ./product-service/docker-compose.yml
  - path: ./fridge-service/docker-compose.yml
  - path: ./user-service/docker-compose.yml
  - path: ./recommendation-service/docker-compose.yml
  - path: ./notification-service/docker-compose.yml
```

**Requisito:** Docker Compose v2.24+ para soporte de `include`.

---

## Perfiles Spring: Mock vs Real

Los microservicios que dependen de otros servicios usan **Spring Profiles** para alternar entre clientes mock y reales.

### Convención

| Perfil | Uso | Cuándo se activa |
|---|---|---|
| `dev` | Clientes mock con datos hardcodeados | Desarrollo individual, pruebas unitarias |
| `!dev` (o `prod`, `staging`) | Clientes OpenFeign reales | Integración, producción |

### Estructura por Cliente

```
client/
├── OrdenClient.java              ← Interfaz (contrato)
├── OrdenClientMockImpl.java      ← @Profile("dev"), datos falsos
└── OrdenClientFeignImpl.java     ← @Profile("!dev"), llamada real
```

### application.yml

```yaml
spring:
  profiles:
    active: dev   # Cambiar a prod/staging para integración
```

---

## Checklist para Nuevos Microservicios

Cuando un integrante cree un nuevo microservicio, debe:

1. [ ] Crear `docker-compose.yml` propio con sus contenedores de DB
2. [ ] Agregar `include` en el `docker-compose.yml` raíz
3. [ ] Configurar `application.yml` con connection strings apuntando a sus contenedores
4. [ ] Si depende de otro servicio: crear interfaz + mock impl (`@Profile("dev")`) + feign impl (`@Profile("!dev")`)
5. [ ] Documentar en esta tabla los nombres de sus contenedores
