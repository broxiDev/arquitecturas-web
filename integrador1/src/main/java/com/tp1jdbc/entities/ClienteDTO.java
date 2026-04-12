package com.tp1jdbc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un cliente sin su identificador sensible.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String nombre;
    private String email;

    @Override
    public String toString() {
        return String.format("Cliente: %s (%s)", nombre, email);
    }
}
