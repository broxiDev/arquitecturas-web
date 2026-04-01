package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief Factura
 * @details Esta clase contiene la estructura de datos de la entidad Factura.
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Factura {

    private int idFactura; // identificador unico de la factura
    private int idCliente; // identificador unico del cliente

    /**
     * @brief Constructor parametrizado de la clase Factura. Genera instancia con seteo de ID de factura y ID de cliente al que pertenece.
     * @param idFactura [in] identificador unico de la factura
     * @param idCliente [in] identificador unico del cliente al que pertenece la factura
     */
    public Factura(int idFactura, int idCliente) {
        this.idFactura = idFactura;
        this.idCliente = idCliente;
    }
}

