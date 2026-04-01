-- Script de inicialización de base de datos
-- Se ejecuta automáticamente al crear el contenedor

CREATE DATABASE IF NOT EXISTS integrador1_db;
USE integrador1_db;

CREATE TABLE IF NOT EXISTS cliente (
    idCliente INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL
);

