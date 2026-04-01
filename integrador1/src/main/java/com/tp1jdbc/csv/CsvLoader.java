package com.tp1jdbc.csv;

import com.tp1jdbc.entities.Cliente;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    /**
     * Lee clientes.csv desde resources y devuelve una lista de Cliente.
     * Columnas esperadas: idCliente, nombre, email
     */
    public static List<Cliente> cargarClientes() throws IOException {
        List<Cliente> clientes = new ArrayList<>();

        Reader reader = new InputStreamReader(
            CsvLoader.class.getClassLoader().getResourceAsStream("clientes.csv")
        );

        try (CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(reader)) {

            for (CSVRecord row : parser) {
                int idCliente  = Integer.parseInt(row.get("idCliente"));
                String nombre  = row.get("nombre");
                String email   = row.get("email");
                clientes.add(new Cliente(idCliente, nombre, email));
            }
        }

        return clientes;
    }
}

