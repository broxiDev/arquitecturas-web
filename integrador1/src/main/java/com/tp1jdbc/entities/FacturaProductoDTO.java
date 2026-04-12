package com.tp1jdbc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la relación entre una factura y un producto, incluyendo la cantidad vendida.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaProductoDTO {
    private String nombre;
    private float valor;
    private double recaudacion;

    @Override
    public String toString() {
        return String.format(
                "FacturaProductoDTO:%n" +
                "  Nombre      : %s%n" +
                "  Precio      : $%.2f%n" +
                "  Recaudación : $%.2f",
                nombre, valor, recaudacion);
    }
}