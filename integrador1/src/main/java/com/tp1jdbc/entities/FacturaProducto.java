package com.tp1jdbc.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief FacturaProducto
 * @details Esta clase contiene la estructura de datos de la entidad FacturaProducto.
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class FacturaProducto {

    private int idFactura; // identificador unico de la factura
    private int idProducto; // identificador unico del producto
    private int cantidad; // cantidad de productos

    /**
     * @brief Constructor parametrizado de la clase FacturaProducto. Genera instancia con seteo de ID de factura y ID de producto y cantidad de productos.
     * @param idFactura [in] identificador unico de la factura
     * @param idProducto [in] identificador unico del producto
     * @param cantidad [in] numero entero que representa la cantidad de productos
     */
    public FacturaProducto(int idFactura, int idProducto, int cantidad) {
        this.idFactura = idFactura;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }
}

