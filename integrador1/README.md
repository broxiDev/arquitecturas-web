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
Al levantar el contenedor, `init.sql` se ejecuta automáticamente y crea la tabla `cliente`.
Solo resta insertar los usuarios necesarios.

## Requisitos
- Java 21
- Maven
- Docker Desktop
- Docker Compose