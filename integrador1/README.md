# TP1 - JDBC + MySQL + Docker

## Requisitos
- Java 21
- Maven
- Docker Desktop + Docker Compose

## Estructura del proyecto
```
src/main/java/com/tp1jdbc/
├── entities/        → Cliente, Producto, Factura, FacturaProducto
├── dao/             → ClienteDAO, ProductoDAO, FacturaDAO, FacturaProductoDAO
├── utils/
│   ├── csv/         → CsvLoader (parseo de archivos CSV)
│   └── DataLoader   → inicializarMetadata()
├── Conexion.java    → configuración de la conexión JDBC
└── Main.java        → punto de entrada

src/main/resources/
├── clientes.csv
├── productos.csv
├── facturas.csv
└── facturas-productos.csv
```

## Conexión
| | |
|---|---|
| URL | `jdbc:mysql://localhost:3306/integrador1_db` |
| Usuario | `root` |
| Contraseña | *(vacía)* |

## Pasos para ejecutar

### 1. Levantar la base de datos
```bash
docker-compose up --build -d
```
Esto ejecuta `init.sql` automáticamente y crea las tablas:
`Cliente`, `Producto`, `Factura`, `Factura_Producto`.

### 2. Cargar los datos (solo la primera vez)
Ejecutar `Main.java` con `DataLoader.inicializarMetadata()` activo.  
Esto lee los 4 CSVs y los persiste en la BD en el orden correcto según las FK:

> `Cliente` → `Producto` → `Factura` → `Factura_Producto`

Una vez cargados los datos, **comentar la llamada** para evitar errores de duplicados:

```java
public static void main(String[] args) {
    // DataLoader.inicializarMetadata(); // ← solo 1 vez, luego comentar
}
```

### 3. Ejecutar normalmente
Con los datos ya cargados, el `main` queda libre para agregar la lógica del TP.
