# TP3 — Registro de Estudiantes · Spring Boot REST

## Requisitos
- Java 21
- Maven
- Docker

## Levantar

```bash
docker-compose up -d
mvn spring-boot:run
```

App: `http://localhost:8001`  
Swagger UI: `http://localhost:8001/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8001/v3/api-docs`

> La primera vez carga los datos desde los CSV automáticamente.

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/estudiantes` | Todos los estudiantes (por apellido) |
| GET | `/estudiantes/lu/{lu}` | Por libreta universitaria |
| GET | `/estudiantes/genero/{genero}` | Por género |
| GET | `/estudiantes/carrera/{nombre}?ciudad=X` | Por carrera y ciudad |
| POST | `/estudiantes` | Dar de alta un estudiante |
| POST | `/estudiantes/{dni}/carreras/{idCarrera}` | Matricular en una carrera |
| GET | `/carreras` | Carreras con inscriptos (desc) |
| GET | `/carreras/reporte` | Inscriptos y egresados por año |

## Ejemplos

**Alta de estudiante**
```json
POST /estudiantes
{
  "dni": 99999999,
  "nombre": "Juan",
  "apellido": "Perez",
  "edad": 22,
  "genero": "Male",
  "ciudad": "Tandil",
  "lu": 12345
}
```

**Matricular**
```json
POST /estudiantes/99999999/carreras/1
{
  "inscripcion": 2024,
  "graduacion": 0,
  "antiguedad": 1
}
```

## Consola MySQL

```bash
docker exec -it arquitecturas_web_container_i3 mysql -u root integrador3_db
```

```sql
SHOW TABLES;
```

## Reset de base de datos

```bash
docker-compose down -v
docker-compose up -d
```
