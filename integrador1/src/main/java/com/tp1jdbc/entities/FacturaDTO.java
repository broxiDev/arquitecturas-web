package com.tp1jdbc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa una factura con información enriquecida del cliente.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private int idFactura;
    private String nombreCliente;

    @Override
    public String toString() {
        return String.format(
                "FacturaDTO:%n" +
                "  ID Factura     : %d%n" +
                "  Nombre Cliente : %s",
                idFactura, nombreCliente);
    }
}
