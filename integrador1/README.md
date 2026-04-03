# TP1 — JDBC + MySQL + Docker

## Requisitos
- Java 21
- Maven
- Docker Desktop

---
## Pasos para ejecutar

### 1. Levantar la base de datos
```bash
 docker-compose up --build -d
```

| Campo      | Valor                                        |
|------------|----------------------------------------------|
| URL        | `jdbc:mysql://localhost:3306/integrador1_db` |
| Usuario    | `root`                                       |
| Contraseña | *(vacía)*                                    |

### 2. Cargar los datos *(solo la primera vez)*
Descomentar en `Main.java`:
```java
DataLoader.inicializarMetadata();
```
Ejecutar el proyecto y volver a comentar la línea para evitar duplicados.

### 3. Ejecutar
Correr `Main.java` normalmente con los datos ya cargados.
