-- Script de inicialización de base de datos
-- Se ejecuta automáticamente al crear el contenedor

CREATE DATABASE IF NOT EXISTS integrador1_db;
USE integrador1_db;

CREATE TABLE IF NOT EXISTS Cliente (
    idCliente INT PRIMARY KEY,
    nombre    VARCHAR(500) NOT NULL,
    email     VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS Producto (
    idProducto INT PRIMARY KEY,
    nombre     VARCHAR(45) NOT NULL,
    valor      FLOAT       NOT NULL
);

CREATE TABLE IF NOT EXISTS Factura (
    idFactura INT PRIMARY KEY,
    idCliente INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente)
);

CREATE TABLE IF NOT EXISTS Factura_Producto (
    idFactura  INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad   INT NOT NULL,
    PRIMARY KEY (idFactura, idProducto),
    FOREIGN KEY (idFactura)  REFERENCES Factura(idFactura),
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);
