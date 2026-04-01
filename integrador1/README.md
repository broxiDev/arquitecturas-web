# TP1 - JDBC + MySQL + Docker

## Levantar la base de datos
```bash
docker-compose up --build -d
```

## Conexión
| | |
|---|---|
| URL | `jdbc:mysql://localhost:3306/integrador1_db` |
| Usuario | `root` |
| Contraseña | *(vacía)* |

## Base de datos
Al levantar el contenedor, `init.sql` se ejecuta automáticamente y crea las tablas:
`Cliente`, `Producto`, `Factura`, `Factura_Producto`.

## Inicializar metadata (solo la primera vez)
Los datos de los CSV se cargan ejecutando el método `inicializarMetadata()` desde `Main.java`.

**Pasos:**
1. Asegurarse de que el contenedor esté corriendo (`docker-compose up -d`)
2. Ejecutar `Main.java` con `inicializarMetadata()` activo en el `main`
3. Una vez cargados los datos, **comentar la llamada** para evitar errores de duplicados en ejecuciones futuras:

```java
public static void main(String[] args) {
    // inicializarMetadata(); // ← ejecutar solo 1 vez, luego comentar
}
```

## Requisitos
- Java 21
- Maven
- Docker Desktop
- Docker Compose