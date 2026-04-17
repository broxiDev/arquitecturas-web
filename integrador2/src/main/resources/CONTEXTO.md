# Contexto - Integrador TP2 ArqWeb (TUDAI)

## 📋 Consignas del Ejercicio Integrador

### Punto 1 — Diagrama de objetos y DER
Diseñar el modelo orientado a objetos y el DER para un registro de estudiantes con:
- nombres, apellido, edad, género, número de documento, ciudad de residencia
- número de libreta universitaria
- carrera(s) en la que está inscripto
- antigüedad en cada carrera
- si se graduó o no

### Punto 2 — Consultas JPQL (implementar en Repository, no en código Java)
| # | Descripción |
|---|-------------|
| 2a | Dar de alta un estudiante |
| 2b | Matricular un estudiante en una carrera |
| 2c | Recuperar todos los estudiantes con algún criterio de ordenamiento simple |
| 2d | Recuperar un estudiante por número de libreta universitaria |
| 2e | Recuperar todos los estudiantes por género |
| 2f | Recuperar las carreras con estudiantes inscriptos, ordenadas por cantidad de inscriptos |
| 2g | Recuperar estudiantes de una carrera, filtrado por ciudad de residencia |

### Punto 3 — Reporte de carreras
- Para cada carrera: inscriptos y egresados **por año** (antigüedad como año proxy)
- Ordenar carreras **alfabéticamente**
- Presentar años de manera **cronológica** (ascendente)

> ⚠️ **Nota clave**: las consultas deben resolverse mayormente en **JPQL**, no en código Java.

---

## 🗂️ Estructura de entidades ya definidas

### `Estudiante`
```
id (PK, autogenerado)
nombres        VARCHAR(80) NOT NULL
apellido       VARCHAR(80) NOT NULL
edad           INT NOT NULL
genero         ENUM: FEMENINO | MASCULINO | OTRO | PREFIERO_NO_DECIRLO
numeroDocumento          VARCHAR(20) UNIQUE NOT NULL
ciudadResidencia         VARCHAR(100) NOT NULL
numeroLibretaUniversitaria VARCHAR(30) UNIQUE NOT NULL
inscripciones  → List<Inscripcion> (OneToMany, mappedBy="estudiante")
```

### `Carrera`
```
id (PK, autogenerado)
nombre         VARCHAR(120) UNIQUE NOT NULL
inscripciones  → List<Inscripcion> (OneToMany, mappedBy="carrera")
```

### `Inscripcion`
```
id (PK, autogenerado)
estudiante     → ManyToOne FK estudiante_id
carrera        → ManyToOne FK carrera_id
antiguedadEnAnios  INT NOT NULL  ← CLAVE para el reporte por año
graduado           BOOLEAN NOT NULL DEFAULT FALSE
UNIQUE (estudiante_id, carrera_id)
```

---

## 🏗️ Estructura de archivos a crear (patrón Ejemplo 8)

```
src/main/java/com/tp2jpa/
├── entities/
│   ├── Estudiante.java        ✅ ya existe
│   ├── Carrera.java           ✅ ya existe
│   └── Inscripcion.java       ✅ ya existe
├── factory/
│   └── JPAUtil.java           🔧 a crear
├── dto/
│   ├── EstudianteDTO.java     🔧 a crear  (nombres, apellido, LU, género, ciudad)
│   └── CarreraReporteDTO.java 🔧 a crear  (carrera, anio, inscriptos, egresados)
├── repository/
│   ├── EstudianteRepository.java   🔧 a crear
│   ├── CarreraRepository.java      🔧 a crear
│   └── InscripcionRepository.java  🔧 a crear
└── Main.java                  🔧 a crear
```

---

## 🔌 Infraestructura (docker-compose + persistence.xml)

| Motor      | Puerto externo | persistence-unit    |
|------------|---------------|---------------------|
| MySQL 8.0  | **3306**      | `integrador2-mysql` |
| MariaDB 10.11 | **3311**   | `integrador2-mariadb` |

> ⚠️ **Bug en persistence.xml**: la URL de MySQL dice puerto `3310` pero docker-compose mapea al `3306`. Hay que corregir a `3306`.

DB: `integrador2_db`  
User: `root` / Password: vacío

---

## 📐 Patrón a seguir (Ejemplo 8: `8.otro_ejemplo_jpa`)

### JPAUtil
```java
public class JPAUtil {
    private static final EntityManagerFactory emf;
    static { emf = Persistence.createEntityManagerFactory("integrador2-mysql"); }
    public static EntityManager getEntityManager() { return emf.createEntityManager(); }
    public static void close() { if (emf != null && emf.isOpen()) emf.close(); }
}
```

### Patrón Repository
```java
public List<XxxDTO> consulta(String param) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        return em.createQuery("SELECT new com.tp2jpa.dto.XxxDTO(...) FROM ...", XxxDTO.class)
                 .setParameter("p", param)
                 .getResultList();
    } finally {
        em.close();
    }
}
```

### Patrón persist (alta/modificación)
```java
public void guardar(Entidad e) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        em.persist(e);
        em.getTransaction().commit();
    } catch (Exception ex) {
        em.getTransaction().rollback();
        ex.printStackTrace();
    } finally {
        em.close();
    }
}
```

### DTOs sin Lombok (el proyecto no tiene Lombok)
Los DTOs se hacen con constructor con todos los parámetros + getters + toString() manual.

---

## 📝 JPQL de cada consulta

### 2c — Todos los estudiantes ordenados (por apellido)
```jpql
SELECT e FROM Estudiante e ORDER BY e.apellido ASC
```

### 2d — Por número de libreta universitaria
```jpql
SELECT e FROM Estudiante e WHERE e.numeroLibretaUniversitaria = :lu
```

### 2e — Por género
```jpql
SELECT e FROM Estudiante e WHERE e.genero = :genero ORDER BY e.apellido ASC
```

### 2f — Carreras con inscriptos, ordenadas por cantidad de inscriptos DESC
```jpql
SELECT c.nombre, COUNT(i) 
FROM Carrera c JOIN c.inscripciones i 
GROUP BY c.id, c.nombre 
ORDER BY COUNT(i) DESC
```
→ Retorna `Object[]` o un DTO `CarreraInscriptosDTO(nombre, cantidad)`

### 2g — Estudiantes de una carrera filtrado por ciudad
```jpql
SELECT e FROM Estudiante e 
JOIN e.inscripciones i 
JOIN i.carrera c 
WHERE c.nombre = :carrera AND e.ciudadResidencia = :ciudad
ORDER BY e.apellido ASC
```

### Punto 3 — Reporte por carrera, por año, inscriptos y egresados
```jpql
SELECT c.nombre, i.antiguedadEnAnios, COUNT(i), SUM(CASE WHEN i.graduado = true THEN 1 ELSE 0 END)
FROM Carrera c JOIN c.inscripciones i
GROUP BY c.nombre, i.antiguedadEnAnios
ORDER BY c.nombre ASC, i.antiguedadEnAnios ASC
```
→ Retorna `Object[]` con [carrera, año, totalInscriptos, egresados]
→ Usar `CarreraReporteDTO(String carrera, int anio, long inscriptos, long egresados)`

---

## 🧪 Datos de prueba para Main (sin CSV)

```
Carreras: "Sistemas", "Contador Público", "Abogacía"

Estudiantes:
- Juan Pérez, 22, MASCULINO, dni=11111111, Tandil, LU=LU001
- Ana García, 24, FEMENINO, dni=22222222, Tandil, LU=LU002
- Carlos López, 21, MASCULINO, dni=33333333, Buenos Aires, LU=LU003
- María Díaz, 23, FEMENINO, dni=44444444, Buenos Aires, LU=LU004
- Pedro Gómez, 25, MASCULINO, dni=55555555, Mar del Plata, LU=LU005

Inscripciones:
- Juan → Sistemas, 3 años, graduado=false
- Ana → Sistemas, 5 años, graduado=true
- Ana → Contaduría, 2 años, graduado=false
- Carlos → Abogacía, 4 años, graduado=false
- María → Sistemas, 5 años, graduado=true
- Pedro → Sistemas, 1 año, graduado=false
- Pedro → Abogacía, 2 años, graduado=false
```

---

## 🔑 Notas importantes

1. **`antiguedadEnAnios`** se usa como proxy del "año" en el reporte. En la consigna dice "inscriptos y egresados por año" → GROUP BY esa columna.
2. **`graduado`** es `Boolean` en Java → en JPQL se puede filtrar con `i.graduado = true`.
3. **SUM con CASE** en JPQL: `SUM(CASE WHEN i.graduado = true THEN 1 ELSE 0 END)` — funciona en Hibernate 6.
4. El proyecto **no usa Lombok** → los DTOs necesitan constructor completo + getters + toString() manuales.
5. La entity `Inscripcion` ya tiene setter `setEstudiante()` pero no tiene `setCarrera()` → hay que agregarlo para 2b.
6. Para el reporte del punto 3, si JPQL con CASE no funciona, alternativa: hacer dos queries separadas (inscriptos totales y graduados) y combinar en Java.

