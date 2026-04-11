package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un Cliente del sistema.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Cliente {

    private int idCliente;
    private String nombre;
    private String email;

    /**
     * Crea una instancia de {@code Cliente} con todos sus atributos.
     *
     * @param idCliente identificador único del cliente
     * @param nombre    nombre del cliente
     * @param email     email del cliente
     */
    public Cliente(int idCliente, String nombre, String email) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
    }
}
