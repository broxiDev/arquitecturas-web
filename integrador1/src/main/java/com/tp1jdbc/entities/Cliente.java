package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief Cliente
 * @details Esta clase contiene la estructura de datos de la entidad Cliente.
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Cliente {

    private int idCliente; // identificador unico del cliente
    private String nombre; // nombre del cliente
    private String email; // email del cliente

    /**
     * @brief Constructor parametrizado de la clase Cliente. Genera instancia con seteo de ID de cliente, nombre y email.
     * @param idCliente [in] identificador unico del cliente
     * @param nombre [in] nombre del cliente
     * @param email [in] email del cliente
     */
    public Cliente(int idCliente, String nombre, String email) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
    }
}

