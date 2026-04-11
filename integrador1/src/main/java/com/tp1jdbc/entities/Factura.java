package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una Factura del sistema.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Factura {

    private int idFactura;
    private int idCliente;

    /**
     * Crea una instancia de {@code Factura} con todos sus atributos.
     *
     * @param idFactura identificador único de la factura
     * @param idCliente identificador del cliente al que pertenece la factura
     */
    public Factura(int idFactura, int idCliente) {
        this.idFactura = idFactura;
        this.idCliente = idCliente;
    }
}
