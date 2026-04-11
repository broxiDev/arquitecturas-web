package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un Producto del sistema.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Producto {

    private int idProducto;
    private String nombre;
    private float valor;

    /**
     * Crea una instancia de {@code Producto} con todos sus atributos.
     *
     * @param idProducto identificador único del producto
     * @param nombre     nombre del producto
     * @param valor      precio del producto
     */
    public Producto(int idProducto, String nombre, float valor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.valor = valor;
    }
}

