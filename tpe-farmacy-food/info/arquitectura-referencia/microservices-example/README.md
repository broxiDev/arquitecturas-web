# Microservices — Split de Integrador 3

Refactorización del monolito `integrador3` en 3 microservicios independientes usando Spring Cloud.

---

## Arquitectura

```
                        ┌─────────────────────────────────────┐
                        │           CLIENTE / BROWSER          │
                        └──────────────────┬──────────────────┘
                                           │ HTTP :8080
                        ┌──────────────────▼──────────────────┐
                        │            API GATEWAY               │
                        │         (Spring Cloud Gateway)       │
                        │              :8080                   │
                        └────────────┬──────────┬─────────────┘
                                     │          │
             /api/estudiantes/**      │          │  /api/carreras/**
             (rutas generales)        │          │  /api/estudiantes/*/carreras/**
                                     │          │  /api/estudiantes/carrera/**
                    ┌────────────────▼──┐   ┌───▼────────────────┐
                    │  msvc-estudiantes  │   │   msvc-carreras    │
                    │   Spring Boot      │   │   Spring Boot      │
                    │      :8001         │   │      :8002         │
                    └────────┬───────────┘   └──────┬────────────┘
                             │  Feign (HTTP interno) │
                             │◄──────────────────────┘
                             │  GET /api/estudiantes/{dni}
                             │  GET /api/estudiantes/bulk
                    ┌────────▼──────────┐   ┌────────────────────┐
                    │  msvc_estudiantes  │   │   msvc_carreras_db  │
                    │       _db          │   │                    │
                    │   MySQL :3306      │   │   MySQL :3307      │
                    └───────────────────┘   └────────────────────┘
```

### Responsabilidades

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `api-gateway` | 8080 | — | Enruta requests al servicio correcto |
| `msvc-estudiantes` | 8001 | `msvc_estudiantes_db` (MySQL :3306) | CRUD de estudiantes |
| `msvc-carreras` | 8002 | `msvc_carreras_db` (MySQL :3307) | Carreras, matrículas, reportes |

### Comunicación entre servicios

`msvc-carreras` llama a `msvc-estudiantes` via **OpenFeign** en 2 casos:

---

#### Caso 1 — Matricular estudiante

```
Cliente
  │
  │  POST /api/estudiantes/{dni}/carreras/{idCarrera}
  ▼
api-gateway :8080
  │
  │  POST /api/estudiantes/{dni}/carreras/{idCarrera}
  ▼
msvc-carreras :8002
  │
  │  1. GET /api/estudiantes/{dni}        ← Feign (valida que el estudiante existe)
  ▼
msvc-estudiantes :8001
  │
  │  200 OK { estudiante } / 404 Not Found
  ▼
msvc-carreras :8002
  │
  │  2. Si existe → guarda EstudianteCarrera en msvc_carreras_db
  │     Si no existe → devuelve 404 al cliente
  ▼
Cliente
  │  201 Created { inscripcion } / 404 Not Found
```

---

#### Caso 2 — Buscar estudiantes por carrera + ciudad

```
Cliente
  │
  │  GET /api/estudiantes/carrera/{carrera}?ciudad={ciudad}
  ▼
api-gateway :8080
  │
  │  GET /api/estudiantes/carrera/{carrera}?ciudad={ciudad}
  ▼
msvc-carreras :8002
  │
  │  1. Consulta local: busca en EstudianteCarrera los DNIs de esa carrera
  │     (msvc_carreras_db)
  │
  │  2. GET /api/estudiantes/bulk?dnis=11,22,33...  ← Feign (obtiene datos completos)
  ▼
msvc-estudiantes :8001
  │
  │  200 OK [ { estudiante }, { estudiante }, ... ]
  ▼
msvc-carreras :8002
  │
  │  3. Filtra por ciudad en memoria → ordena por apellido
  ▼
Cliente
  │  200 OK [ estudiantes filtrados ]
```

### Ruteo del Gateway

Las rutas se evalúan en orden (de más específica a más general):

```
/api/estudiantes/{dni}/carreras/**  →  msvc-carreras  (matricular)
/api/estudiantes/carrera/**         →  msvc-carreras  (buscar por carrera+ciudad)
/api/carreras/**                    →  msvc-carreras
/api/estudiantes/**                 →  msvc-estudiantes
```

---

## Requisitos

- Java 21
- Maven 3.8+
- Docker

---

## Cómo ejecutar

### 1. Levantar las bases de datos

```bash
docker-compose up -d
```

Levanta 2 contenedores MySQL:
- `msvc_estudiantes_db` en `localhost:3306`
- `msvc_carreras_db` en `localhost:3307`

### 2. Levantar msvc-estudiantes

```bash
cd msvc-estudiantes
mvn spring-boot:run
```

- Arranca en `localhost:8001`
- Carga `estudiantes.csv` automáticamente al iniciar (solo si la DB está vacía)
- Swagger: [http://localhost:8001/swagger-ui.html](http://localhost:8001/swagger-ui.html)

### 3. Levantar msvc-carreras

```bash
cd msvc-carreras
mvn spring-boot:run
```

- Arranca en `localhost:8002`
- Carga `carreras.csv` y `estudianteCarrera.csv` automáticamente al iniciar
- Swagger: [http://localhost:8002/swagger-ui.html](http://localhost:8002/swagger-ui.html)

### 4. Levantar api-gateway

```bash
cd api-gateway
mvn spring-boot:run
```

- Arranca en `localhost:8080`
- No tiene Swagger propio — usa los de cada servicio para explorar

## Swagger UI

| Servicio | URL |
|---|---|
| msvc-estudiantes | [http://localhost:8001/swagger-ui.html](http://localhost:8001/swagger-ui.html) |
| msvc-carreras | [http://localhost:8002/swagger-ui.html](http://localhost:8002/swagger-ui.html) |

> El gateway no expone Swagger propio — usá los de cada servicio directamente.

---

> **Orden recomendado:** bases de datos → msvc-estudiantes → msvc-carreras → api-gateway.
> msvc-carreras llama a msvc-estudiantes via Feign, por lo que conviene que este último ya esté corriendo.

---

## Endpoints

Todos los endpoints se consumen a través del gateway en `localhost:8080`.

| Método | Path | Descripción | Servicio destino |
|---|---|---|---|
| `POST` | `/api/estudiantes` | a) Alta de estudiante | msvc-estudiantes |
| `POST` | `/api/estudiantes/{dni}/carreras/{idCarrera}` | b) Matricular estudiante | msvc-carreras |
| `GET` | `/api/estudiantes` | c) Todos los estudiantes por apellido | msvc-estudiantes |
| `GET` | `/api/estudiantes/lu/{lu}` | d) Estudiante por libreta universitaria | msvc-estudiantes |
| `GET` | `/api/estudiantes/genero/{genero}` | e) Estudiantes por género | msvc-estudiantes |
| `GET` | `/api/carreras` | f) Carreras con cantidad de inscriptos | msvc-carreras |
| `GET` | `/api/estudiantes/carrera/{nombre}?ciudad=` | g) Estudiantes de una carrera por ciudad | msvc-carreras |
| `GET` | `/api/carreras/reporte` | h) Reporte anual inscriptos y egresados | msvc-carreras |

### Ejemplo — Matricular un estudiante

```bash
curl -X POST http://localhost:8080/api/estudiantes/12345670/carreras/1 \
  -H "Content-Type: application/json" \
  -d '{"inscripcion": 2022, "graduacion": 0, "antiguedad": 3}'
```

### Ejemplo — Buscar por carrera y ciudad

```bash
curl "http://localhost:8080/api/estudiantes/carrera/TUDAI?ciudad=Paquera"
```

---

## Compilar sin ejecutar

Desde la raíz del proyecto:

```bash
mvn clean compile     # solo compila
mvn clean package     # genera los JARs
```

---

## Apagar todo

```bash
docker-compose down        # detiene y elimina contenedores
docker-compose down -v     # idem + elimina los volúmenes (borra datos)
```
