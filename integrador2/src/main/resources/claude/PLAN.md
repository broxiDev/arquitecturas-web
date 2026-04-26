# Plan — Completar TPE Integrador 2 (puntos 2b–2g + punto 3)

## Contexto

El módulo `integrador2` ya tiene la mayor parte del trabajo hecho: entidades JPA, repositorios, DTOs, schema MySQL, CSVs y un `DataLoader` que persiste los datos. El enunciado pide implementar el sistema de inscripciones universitarias con queries JPQL.

**Lo que falta concretamente:**
1. Un bug en `persistence.xml` que referencia una clase vieja (`Inscripcion`) inexistente — bloquea cualquier ejecución porque Hibernate no encuentra la entidad.
2. La query del punto 3 (`generarReporte`) agrupa por `antiguedad` (años de seniority) cuando el enunciado pide "por año" calendario — confirmado con el usuario que se reescribe.
3. El `Main.java` solo demuestra los puntos 2a y 2b. Hay que agregar demos para 2c, 2d, 2e, 2f, 2g y el reporte (punto 3) para tener un smoke test end-to-end del enunciado.

Las queries JPQL para 2c–2g ya están escritas y correctas en los repositorios. El trabajo de esta tarea es: arreglar el bug bloqueante, corregir la semántica del reporte y expandir `Main` para ejercitar todo el TP.

---

## Archivos críticos

| Archivo | Acción |
|---|---|
| `integrador2/src/main/resources/META-INF/persistence.xml:12` | Reemplazar `Inscripcion` por `EstudianteCarrera` |
| `integrador2/src/main/java/com/tp2jpa/repository/CarreraRepository.java` | Reescribir `generarReporte()` para usar año calendario |
| `integrador2/src/main/java/com/tp2jpa/Main.java` | Agregar demos para 2c, 2d, 2e, 2f, 2g y punto 3 |

Lo demás (entidades, DTOs, otros repos, `JPAUtil`, `DataLoader`, CSVs) queda intacto — ya está bien.

---

## Paso 1 — Arreglar `persistence.xml` (bloquea runtime)

En `integrador2/src/main/resources/META-INF/persistence.xml:12`, cambiar:

```xml
<class>com.tp2jpa.entities.Inscripcion</class>
```

por:

```xml
<class>com.tp2jpa.entities.EstudianteCarrera</class>
```

Sin esto, cualquier operación que toque la junction table tira `ClassNotFoundException` o ignora la entidad.

---

## Paso 2 — Reescribir `CarreraRepository.generarReporte()` con año calendario

**Decisión confirmada con el usuario:** el "año" del reporte es año calendario, no antigüedad. Una fila por `(carrera, año)` donde:
- `inscriptos` = `COUNT(*)` donde `inscripcion = año`
- `egresados` = `COUNT(*)` donde `graduacion = año` (excluyendo `graduacion = 0`)

**Diseño:** dos queries JPQL que retornan `CarreraReporteDTO` directamente vía `new` en JPQL (pasando `0L` como placeholder para el campo que la query no computa). El merge posterior en Java usa for loops y `Collections.sort` — sin streams ni lambdas.

- Query 1: agrupa por `(carrera, inscripcion)`, retorna `CarreraReporteDTO(nombre, inscripcion, COUNT, 0L)`.
- Query 2: agrupa por `(carrera, graduacion)` donde `graduacion > 0`, retorna `CarreraReporteDTO(nombre, graduacion, 0L, COUNT)`.
- Merge: for loops con `Map<String, long[]>` como acumulador interno; al final construye la lista de DTOs con un for loop y la ordena con `Collections.sort` + Comparator anónimo.

**Lógica JPQL:** todo el filtrado, join, agrupación y conteo está en las dos queries. Java solo funde las dos listas en una — trabajo mínimo.

**En `CarreraRepository.java`, reemplazar el método `generarReporte()` por:**

```java
// Punto 3 — Reporte: inscriptos y egresados por carrera y año calendario
public List<CarreraReporteDTO> generarReporte() {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<CarreraReporteDTO> porInscripcion = em.createQuery(
                "SELECT new com.tp2jpa.dto.CarreraReporteDTO(" +
                "  c.nombreCarrera, i.inscripcion, COUNT(i), 0L) " +
                "FROM Carrera c JOIN c.inscripciones i " +
                "GROUP BY c.nombreCarrera, i.inscripcion",
                CarreraReporteDTO.class
        ).getResultList();

        List<CarreraReporteDTO> porGraduacion = em.createQuery(
                "SELECT new com.tp2jpa.dto.CarreraReporteDTO(" +
                "  c.nombreCarrera, i.graduacion, 0L, COUNT(i)) " +
                "FROM Carrera c JOIN c.inscripciones i " +
                "WHERE i.graduacion > 0 " +
                "GROUP BY c.nombreCarrera, i.graduacion",
                CarreraReporteDTO.class
        ).getResultList();

        // Acumulador: clave = "carrera|año", valor = [inscriptos, egresados]
        Map<String, long[]> acc = new LinkedHashMap<>();

        for (CarreraReporteDTO dto : porInscripcion) {
            String k = dto.getCarrera() + "|" + dto.getAnio();
            acc.put(k, new long[]{dto.getInscriptos(), 0L});
        }
        for (CarreraReporteDTO dto : porGraduacion) {
            String k = dto.getCarrera() + "|" + dto.getAnio();
            long[] vals = acc.get(k);
            if (vals != null) {
                vals[1] = dto.getEgresados();
            } else {
                acc.put(k, new long[]{0L, dto.getEgresados()});
            }
        }

        List<CarreraReporteDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : acc.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            resultado.add(new CarreraReporteDTO(
                    parts[0],
                    Integer.valueOf(parts[1]),
                    entry.getValue()[0],
                    entry.getValue()[1]
            ));
        }

        Collections.sort(resultado, new Comparator<CarreraReporteDTO>() {
            @Override
            public int compare(CarreraReporteDTO a, CarreraReporteDTO b) {
                int cmp = a.getCarrera().compareTo(b.getCarrera());
                if (cmp != 0) return cmp;
                return a.getAnio().compareTo(b.getAnio());
            }
        });

        return resultado;
    } finally {
        em.close();
    }
}
```

Imports a agregar: `java.util.Map`, `java.util.LinkedHashMap`, `java.util.ArrayList`, `java.util.Collections`, `java.util.Comparator`.

**Nota:** el DTO `CarreraReporteDTO` ya tiene los campos correctos (`carrera`, `anio`, `inscriptos`, `egresados`) — no se toca. El `0L` en las queries JPQL de Hibernate 6 es un literal Long válido.

---

## Paso 3 — Expandir `Main.java` con demos para 2c–2g y punto 3

Mantener los demos `darDeAltaEstudiante()` (2a) y `matricularEstudiante()` (2b) tal como están — el usuario confirmó que está OK que se ejecuten cada corrida aunque acumulen duplicados.

**Agregar estos métodos** (todos privados, llamados desde `main()`):

- `listarTodosLosEstudiantes()` → 2c. Llama a `EstudianteRepository.buscarTodos()`, imprime cantidad total y los primeros ~5 con un header claro.
- `buscarEstudiantePorLU()` → 2d. Llama a `buscarPorLU(999001L)` (la LU del estudiante que se da de alta en 2a). Imprime el estudiante o "no encontrado".
- `listarEstudiantesPorGenero()` → 2e. Llama a `buscarPorGenero("Masculino")` y a `buscarPorGenero("Femenino")`. Imprime conteo + primeros resultados de cada uno.
- `listarCarrerasConInscriptos()` → 2f. Llama a `CarreraRepository.buscarCarrerasConInscriptos()`. Imprime tabla `carrera | cantidad` ordenada DESC.
- `listarEstudiantesPorCarreraYCiudad()` → 2g. Llama a `buscarPorCarreraYCiudad("<carrera-existente>", "<ciudad-existente>")`. Usar valores reales del CSV para que devuelva resultados.
- `generarReporteCarreras()` → punto 3. Llama a `CarreraRepository.generarReporte()`. Imprime tabla `carrera | año | inscriptos | egresados`.

**Estructura del `main()`:**

```java
public static void main(String[] args) {
    //poblarDB(); // descomentar solo la primera vez

    System.out.println("\n=== 2a — Alta de estudiante ===");
    darDeAltaEstudiante();

    System.out.println("\n=== 2b — Matricular en carrera ===");
    matricularEstudiante();

    System.out.println("\n=== 2c — Todos los estudiantes (por apellido) ===");
    listarTodosLosEstudiantes();

    System.out.println("\n=== 2d — Estudiante por LU ===");
    buscarEstudiantePorLU();

    System.out.println("\n=== 2e — Estudiantes por género ===");
    listarEstudiantesPorGenero();

    System.out.println("\n=== 2f — Carreras con inscriptos (DESC) ===");
    listarCarrerasConInscriptos();

    System.out.println("\n=== 2g — Estudiantes de carrera por ciudad ===");
    listarEstudiantesPorCarreraYCiudad();

    System.out.println("\n=== Punto 3 — Reporte por carrera y año ===");
    generarReporteCarreras();

    JPAUtil.getEntityManagerFactory().close();
}
```

Antes de implementar 2g, leer un par de filas de `estudiantes.csv` y `carreras.csv` para elegir un par `(carrera, ciudad)` que efectivamente tenga matches.

---

## Paso 4 — Verificación end-to-end

1. **Levantar la BD:**
   ```bash
   cd integrador2 && docker-compose up -d
   ```
2. **Compilar:**
   ```bash
   mvn clean compile
   ```
3. **Poblar la BD una vez** (si está vacía): descomentar `poblarDB()` en `Main.java:14`, correr una vez:
   ```bash
   mvn exec:java -Dexec.mainClass="com.tp2jpa.Main"
   ```
   Debería imprimir el resumen de `DataLoader` (15 carreras, 102+ estudiantes, 109 inscripciones). Volver a comentar `poblarDB()`.
4. **Correr el smoke test completo:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.tp2jpa.Main"
   ```
   **Resultado esperado** — cada sección imprime datos no vacíos:
   - 2a/2b: imprime el estudiante y la matrícula creados.
   - 2c: lista ~104 estudiantes ordenados por apellido.
   - 2d: encuentra "Nahuel Di Fiore" (LU 999001).
   - 2e: split por género, ambos con resultados.
   - 2f: 15 carreras ordenadas DESC.
   - 2g: lista no vacía para el par carrera/ciudad elegido del CSV.
   - Punto 3: una fila por `(carrera, año)`, carreras alfabéticas, años cronológicos.
5. Si algún punto devuelve lista vacía, activar `hibernate.show_sql=true` en `persistence.xml` para ver la SQL generada.
