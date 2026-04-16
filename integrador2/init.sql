CREATE DATABASE IF NOT EXISTS integrador2_db;
USE integrador2_db;

DROP TABLE IF EXISTS inscripcion;
DROP TABLE IF EXISTS estudiante;
DROP TABLE IF EXISTS carrera;

CREATE TABLE IF NOT EXISTS carrera (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_carrera_nombre (nombre)
);

CREATE TABLE IF NOT EXISTS estudiante (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    edad INT NOT NULL,
    genero VARCHAR(30) NOT NULL,
    numeroDocumento VARCHAR(20) NOT NULL,
    ciudadResidencia VARCHAR(100) NOT NULL,
    numeroLibretaUniversitaria VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estudiante_documento (numeroDocumento),
    UNIQUE KEY uk_estudiante_libreta (numeroLibretaUniversitaria),
    CONSTRAINT chk_estudiante_edad CHECK (edad >= 0),
    CONSTRAINT chk_estudiante_genero CHECK (
        genero IN ('FEMENINO', 'MASCULINO', 'OTRO', 'PREFIERO_NO_DECIRLO')
    )
);

CREATE TABLE IF NOT EXISTS inscripcion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estudiante_id BIGINT NOT NULL,
    carrera_id BIGINT NOT NULL,
    antiguedadEnAnios INT NOT NULL,
    graduado BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inscripcion_estudiante_carrera (estudiante_id, carrera_id),
    CONSTRAINT chk_inscripcion_antiguedad CHECK (antiguedadEnAnios >= 0),
    CONSTRAINT fk_inscripcion_estudiante
        FOREIGN KEY (estudiante_id) REFERENCES estudiante(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripcion_carrera
        FOREIGN KEY (carrera_id) REFERENCES carrera(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
