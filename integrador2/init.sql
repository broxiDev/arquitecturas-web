CREATE DATABASE IF NOT EXISTS integrador2_db;
USE integrador2_db;

DROP TABLE IF EXISTS estudiante_carrera;
DROP TABLE IF EXISTS estudiante;
DROP TABLE IF EXISTS carrera;

CREATE TABLE carrera (
    id_carrera  BIGINT NOT NULL AUTO_INCREMENT,
    carrera     VARCHAR(120),
    duracion    INT,
    PRIMARY KEY (id_carrera)
);

CREATE TABLE estudiante (
    dni         BIGINT NOT NULL,
    nombre      VARCHAR(80),
    apellido    VARCHAR(80),
    edad        INT,
    genero      VARCHAR(30),
    ciudad      VARCHAR(100),
    lu          BIGINT,
    PRIMARY KEY (dni)
);

CREATE TABLE estudiante_carrera (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    id_estudiante   BIGINT,
    id_carrera      BIGINT,
    inscripcion     INT,
    graduacion      INT,
    antiguedad      INT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(dni),
    FOREIGN KEY (id_carrera)    REFERENCES carrera(id_carrera)
);
