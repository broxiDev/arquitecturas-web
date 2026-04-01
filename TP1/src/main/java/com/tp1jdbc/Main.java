package com.tp1jdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        PersonaDAO dao = new PersonaDAO();

        // Insertar una persona de ejemplo
        Persona nueva = new Persona("Juan", "Perez", "12345678", LocalDate.of(1990, 5, 20), "juan@email.com", "1234567890");
        try {
            dao.insertar(nueva);
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
        }

/*        // Listar todas las personas
        try {
            List<Persona> personas = dao.listarTodos();
            System.out.println("Personas en la BD:");
            personas.forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
        }*/
    }
}
