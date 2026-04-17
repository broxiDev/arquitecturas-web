CREATE DATABASE IF NOT EXISTS integrador2_db;
USE integrador2_db;

DROP TABLE IF EXISTS inscripcion;
DROP TABLE IF EXISTS estudiante;
DROP TABLE IF EXISTS carrera;

CREATE TABLE carrera (
    id     BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120),
    PRIMARY KEY (id)
);

CREATE TABLE estudiante (
    id                           BIGINT NOT NULL AUTO_INCREMENT,
    nombres                      VARCHAR(80),
    apellido                     VARCHAR(80),
    edad                         INT,
    genero                       CHAR(1),
    numero_documento             VARCHAR(20),
    ciudad_residencia            VARCHAR(100),
    numero_libreta_universitaria VARCHAR(30),
    PRIMARY KEY (id)
);

CREATE TABLE inscripcion (
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    estudiante_id      BIGINT,
    carrera_id         BIGINT,
    antiguedad_en_anios INT,
    graduado           BOOLEAN,
    PRIMARY KEY (id),
    FOREIGN KEY (estudiante_id) REFERENCES estudiante(id),
    FOREIGN KEY (carrera_id)    REFERENCES carrera(id)
);
