package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la relación entre una Factura y un Producto.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class FacturaProducto {

    private int idFactura;
    private int idProducto;
    private int cantidad;

    /**
     * Crea una instancia de {@code FacturaProducto} con todos sus atributos.
     *
     * @param idFactura  identificador único de la factura
     * @param idProducto identificador único del producto
     * @param cantidad   cantidad de unidades del producto en la factura
     */
    public FacturaProducto(int idFactura, int idProducto, int cantidad) {
        this.idFactura = idFactura;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }
}
