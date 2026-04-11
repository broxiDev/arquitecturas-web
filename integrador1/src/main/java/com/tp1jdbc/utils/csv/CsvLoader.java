package com.tp1jdbc.utils.csv;

import com.tp1jdbc.entities.Cliente;
import com.tp1jdbc.entities.Factura;
import com.tp1jdbc.entities.FacturaProducto;
import com.tp1jdbc.entities.Producto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza la lectura de archivos CSV del classpath y su transformación a entidades del dominio.
 *
 * @version 1.0
 */
public class CsvLoader {

    /**
     * Abre un recurso del classpath para su lectura como {@link Reader}.
     *
     * @param archivo nombre del archivo dentro de los recursos
     * @return reader asociado al archivo solicitado
     */
    private static Reader abrirRecurso(String archivo) {
        return new InputStreamReader(
            CsvLoader.class.getClassLoader().getResourceAsStream(archivo)
        );
    }

    /**
     * Carga clientes desde el archivo {@code clientes.csv}.
     *
     * @return lista de clientes leídos desde el CSV
     * @throws IOException si ocurre un error durante la lectura del archivo
     */
    public static List<Cliente> cargarClientes() throws IOException {
        List<Cliente> lista = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(abrirRecurso("clientes.csv"))) {
            for (CSVRecord row : parser) {
                lista.add(new Cliente(
                    Integer.parseInt(row.get("idCliente")),
                    row.get("nombre"),
                    row.get("email")
                ));
            }
        }
        return lista;
    }

    /**
     * Carga productos desde el archivo {@code productos.csv}.
     *
     * @return lista de productos leídos desde el CSV
     * @throws IOException si ocurre un error durante la lectura del archivo
     */
    public static List<Producto> cargarProductos() throws IOException {
        List<Producto> lista = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(abrirRecurso("productos.csv"))) {
            for (CSVRecord row : parser) {
                lista.add(new Producto(
                    Integer.parseInt(row.get("idProducto")),
                    row.get("nombre"),
                    Float.parseFloat(row.get("valor"))
                ));
            }
        }
        return lista;
    }

    /**
     * Carga facturas desde el archivo {@code facturas.csv}.
     *
     * @return lista de facturas leídas desde el CSV
     * @throws IOException si ocurre un error durante la lectura del archivo
     */
    public static List<Factura> cargarFacturas() throws IOException {
        List<Factura> lista = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(abrirRecurso("facturas.csv"))) {
            for (CSVRecord row : parser) {
                lista.add(new Factura(
                    Integer.parseInt(row.get("idFactura")),
                    Integer.parseInt(row.get("idCliente"))
                ));
            }
        }
        return lista;
    }

    /**
     * Carga las relaciones factura-producto desde el archivo {@code facturas-productos.csv}.
     *
     * @return lista de relaciones factura-producto leídas desde el CSV
     * @throws IOException si ocurre un error durante la lectura del archivo
     */
    public static List<FacturaProducto> cargarFacturasProductos() throws IOException {
        List<FacturaProducto> lista = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(abrirRecurso("facturas-productos.csv"))) {
            for (CSVRecord row : parser) {
                lista.add(new FacturaProducto(
                    Integer.parseInt(row.get("idFactura")),
                    Integer.parseInt(row.get("idProducto")),
                    Integer.parseInt(row.get("cantidad"))
                ));
            }
        }
        return lista;
    }
}
