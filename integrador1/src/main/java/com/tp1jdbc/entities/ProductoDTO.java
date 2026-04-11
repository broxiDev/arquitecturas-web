package com.tp1jdbc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

/**
 * @brief ProductoDTO
 * @details DTO que representa un producto junto con su recaudación total.
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private String nombre;
    private float valor;
    private double recaudacion;

    @Override
    public String toString() {
        return String.format(Locale.US,
                "Producto que más recaudó:%n" +
                "  Nombre      : %s%n" +
                "  Precio      : $%.2f%n" +
                "  Recaudación : $%.2f",
                nombre, valor, recaudacion);
    }
}
