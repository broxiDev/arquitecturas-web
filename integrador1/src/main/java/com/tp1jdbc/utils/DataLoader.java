package com.tp1jdbc.utils;

import com.tp1jdbc.utils.csv.CsvLoader;
import com.tp1jdbc.dao.*;
import com.tp1jdbc.entities.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DataLoader {

    /**
     * Lee los 4 CSVs y los persiste en la BD.
     * El orden respeta las FK: Cliente → Producto → Factura → Factura_Producto
     * IMPORTANTE: ejecutar solo 1 vez, luego comentar la llamada en Main.
     */
    public static void inicializarMetadata() {
        try {
            // 1. Clientes
            List<Cliente> clientes = CsvLoader.cargarClientes();
            ClienteDAO clienteDAO = new ClienteDAO();
            for (Cliente c : clientes) clienteDAO.insertar(c);
            System.out.println("✓ Clientes insertados: " + clientes.size());

            // 2. Productos
            List<Producto> productos = CsvLoader.cargarProductos();
            ProductoDAO productoDAO = new ProductoDAO();
            for (Producto p : productos) productoDAO.insertar(p);
            System.out.println("✓ Productos insertados: " + productos.size());

            // 3. Facturas (depende de Cliente)
            List<Factura> facturas = CsvLoader.cargarFacturas();
            FacturaDAO facturaDAO = new FacturaDAO();
            for (Factura f : facturas) facturaDAO.insertar(f);
            System.out.println("✓ Facturas insertadas: " + facturas.size());

            // 4. Factura_Producto (depende de Factura y Producto)
            List<FacturaProducto> fps = CsvLoader.cargarFacturasProductos();
            FacturaProductoDAO fpDAO = new FacturaProductoDAO();
            for (FacturaProducto fp : fps) fpDAO.insertar(fp);
            System.out.println("✓ Factura_Producto insertados: " + fps.size());

        } catch (IOException e) {
            System.err.println("Error al leer CSV: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al insertar en BD: " + e.getMessage());
        }
    }
}

