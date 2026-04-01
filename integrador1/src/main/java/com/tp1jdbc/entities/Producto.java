package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief Producto
 * @details Esta clase contiene la estructura de datos de la entidad Producto.
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Producto {

    private int idProducto; // identificador unico del producto
    private String nombre; // nombre del producto
    private float valor; // costo del producto

    /**
     * @brief Constructor parametrizado de la clase Producto. Genera instancia con seteo de ID de producto, nombre del producto y costo.
     * @param idProducto [in] identificador unico del producto
     * @param nombre [in] nombre del producto
     * @param valor [in] costo del producto
     */
    public Producto(int idProducto, String nombre, float valor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.valor = valor;
    }
}

