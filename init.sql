-- Script de inicialización de base de datos
-- Se ejecuta automáticamente al crear el contenedor

CREATE DATABASE IF NOT EXISTS arquitecturas_web;
USE arquitecturas_web;

CREATE TABLE IF NOT EXISTS persona (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    fecha_nacimiento DATE,
    email VARCHAR(150),
    telefono VARCHAR(30)
);
