package com.tp1jdbc;

import com.tp1jdbc.csv.CsvLoader;
import com.tp1jdbc.dao.ClienteDAO;
import com.tp1jdbc.entities.Cliente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ClienteDAO dao = new ClienteDAO();

        // 1. Leer clientes desde el CSV
        try {
            List<Cliente> clientes = CsvLoader.cargarClientes();
            System.out.println("Clientes leídos del CSV: " + clientes.size());

            // 2. Persistir cada cliente en la BD
            for (Cliente c : clientes) {
                dao.insertar(c);
            }
            System.out.println("Todos los clientes fueron insertados.");

        } catch (IOException e) {
            System.err.println("Error al leer el CSV: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al insertar en la BD: " + e.getMessage());
        }
    }
}
