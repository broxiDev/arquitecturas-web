# Integrador 2 — Contexto para Claude

## Qué es este proyecto

TPE Integrador 2 de la materia **Arquitecturas Web (TUDAI)**. Sistema de gestión de inscripciones universitarias: estudiantes se inscriben a carreras. Sin Spring — puro JPA/Hibernate standalone.

**Stack:** Java 21 · Maven · Hibernate 6.5.3 · MySQL 8.0 (Docker, puerto 3306) · Lombok · sin frameworks web.

---

## Estructura de packages

```
com.tp2jpa/
  Main.java                            ← entry point (HOY solo demuestra punto 2a)
  factory/JPAUtil.java                 ← singleton EntityManagerFactory
  entities/
    Estudiante.java                    ← PK = dni (Long, no auto-generado)
    Carrera.java                       ← PK = idCarrera (Long, no auto-generado)
    EstudianteCarrera.java             ← tabla junction, PK = id (IDENTITY auto-gen)
  dto/
    EstudianteDTO.java                 ← nombre, apellido, lu, genero, ciudad
    CarreraInscriptosDTO.java          ← nombre, cantidadInscriptos
    CarreraReporteDTO.java             ← carrera, anio, inscriptos, egresados
  repository/
    EstudianteRepository.java          ← puntos 2a, 2c, 2d, 2e, 2g
    CarreraRepository.java             ← punto 2f + reporte punto 3
    EstudianteCarreraRepository.java   ← punto 2b (matricular)
```

---

## Schema SQL

```sql
carrera (
  id_carrera  BIGINT PK,
  carrera     VARCHAR(120),
  duracion    INT
)

estudiante (
  dni         BIGINT PK,    -- no auto-generado
  nombre      VARCHAR(80),
  apellido    VARCHAR(80),
  edad        INT,
  genero      VARCHAR(30),
  ciudad      VARCHAR(100),
  lu          BIGINT
)

estudiante_carrera (
  id            BIGINT PK AUTO_INCREMENT,
  id_estudiante BIGINT FK → estudiante(dni),
  id_carrera    BIGINT FK → carrera(id_carrera),
  inscripcion   INT,        -- año de inscripción
  graduacion    INT,        -- año de graduación (0 = no graduado)
  antiguedad    INT         -- años en la carrera
)
```

DB: `integrador2_db` · user: `root` · password: vacío · puerto: 3306

---

## Estado actual (última sesión: 2026-04-25)

### Completo
- Schema SQL (`init.sql`) y entidades JPA mapeadas
- `JPAUtil` factory
- Los 3 repositorios con todas las JPQL (puntos 2a–2g + reporte punto 3)
- Los 3 DTOs con Lombok
- CSV de datos: `carreras.csv` (15 carreras), `estudiantes.csv` (102 estudiantes), `estudianteCarrera.csv` (109 inscripciones)

### Pendiente / incompleto

| Prioridad | Tarea |
|-----------|-------|
| 🔴 Alta | **Corregir `persistence.xml`**: referencia la clase vieja `Inscripcion`, debe decir `EstudianteCarrera`. Rompe en runtime al levantar el EMF. |
| 🔴 Alta | **Cargador de CSV**: los CSV existen pero no hay código que los importe a la BD. Sin datos no se pueden probar las queries. |
| 🟡 Media | **Expandir `Main.java`**: agregar llamadas a puntos 2b–2g y al reporte del punto 3 para demostrar todo el enunciado. |

---

## Bug crítico — persistence.xml

**Archivo:** `src/main/resources/META-INF/persistence.xml`

La línea que registra la entidad junction dice:
```xml
<class>com.tp2jpa.entities.Inscripcion</class>
```
Debe ser:
```xml
<class>com.tp2jpa.entities.EstudianteCarrera</class>
```
La clase se renombró en el commit `d0ca8a7` pero el XML no se actualizó.

---

## Queries JPQL implementadas (repositorios)

| Punto | Repositorio | Método |
|-------|-------------|--------|
| 2a | EstudianteRepository | `guardar(Estudiante e)` |
| 2b | EstudianteCarreraRepository | `matricular(Estudiante e, Carrera c, int anio)` |
| 2c | EstudianteRepository | `buscarTodos()` → ordenados por apellido |
| 2d | EstudianteRepository | `buscarPorLU(Long lu)` |
| 2e | EstudianteRepository | `buscarPorGenero(String genero)` |
| 2f | CarreraRepository | `buscarCarrerasConInscriptos()` → CarreraInscriptosDTO DESC |
| 2g | EstudianteRepository | `buscarPorCarreraYCiudad(String carrera, String ciudad)` |
| 3  | CarreraRepository | `generarReporte()` → CarreraReporteDTO por año |

El reporte del punto 3 usa `GROUP BY + CASE WHEN` en JPQL para contar egresados (graduacion > 0).

---

## Datos CSV disponibles

- `carreras.csv`: id_carrera, carrera, duracion — 15 filas (IDs 1-15). Incluye TUDAI (2 años), Ingeniería de Sistemas (5), Medicina (6), etc.
- `estudiantes.csv`: dni, nombre, apellido, edad, genero, ciudad, lu — 102 filas. Géneros mixtos (Male/Female/Polygender/Masculino/Femenino).
- `estudianteCarrera.csv`: id, id_estudiante, id_carrera, inscripcion, graduacion, antiguedad — 109 filas. inscripcion = año (2013-2025), graduacion = 0 si no graduó.

**Problemas de calidad en CSV:**
- Fila 91: `inscripcion=202` (debería ser un año de 4 dígitos)
- Fila 104: `id_estudiante=6397408` no existe en estudiantes.csv

---

## Próximos pasos recomendados

1. Fix `persistence.xml` (1 línea).
2. Crear `CsvLoader.java` (o método en Main) que lea los CSV con `BufferedReader` y persista entidades en orden: primero carreras → estudiantes → inscripciones.
3. Probar que levanta el EMF sin errores.
4. Expandir `Main.java` para llamar a cada punto del enunciado e imprimir resultados.
5. Verificar output del reporte punto 3 contra los CSV.

---

## Notas de implementación

- `hbm2ddl.auto=update` en persistence.xml → Hibernate actualiza el schema automáticamente al arrancar. No hace falta correr `init.sql` manualmente si la BD está vacía.
- Sin connection pooling en JPAUtil — OK para un TP universitario.
- Lombok está en uso (aunque CONTEXTO.md viejo decía que no había Lombok — sí hay, ver pom.xml).
- Las consultas usan constructor expressions JPQL: `SELECT new com.tp2jpa.dto.XxxDTO(...)`.
