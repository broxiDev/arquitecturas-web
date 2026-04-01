package com.tp1jdbc;

import com.tp1jdbc.dao.ClienteDAO;
import com.tp1jdbc.entities.Cliente;

import java.sql.SQLException;


public class Main {

    public static void main(String[] args) {
        ClienteDAO dao = new ClienteDAO();

        // Insertar un cliente de ejemplo
        Cliente nuevo = new Cliente("Juan Perez 2", "juan@emasdail.com");
        try {
            dao.insertar(nuevo);
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
        }

        // Listar todos los clientes
/*        try {
            List<Cliente> clientes = dao.listarTodos();
            System.out.println("Clientes en la BD:");
            clientes.forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
        }*/
    }
}
