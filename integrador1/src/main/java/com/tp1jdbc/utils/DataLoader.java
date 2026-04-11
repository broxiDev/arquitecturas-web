package com.tp1jdbc.utils;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;
import com.tp1jdbc.factory.AbstractFactory;
import com.tp1jdbc.utils.csv.CsvLoader;
import com.tp1jdbc.entities.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Utilidad para cargar datos iniciales desde archivos CSV hacia la base de datos.
 * El orden de inserción respeta las claves foráneas: Cliente → Producto → Factura → Factura_Producto.
 *
 * @version 1.0
 */
public class DataLoader {

    /**
     * Lee los cuatro archivos CSV y persiste su contenido en la base de datos.
     * <p>
     * <strong>Importante:</strong> ejecutar solo una vez; comentar la llamada en {@code Main} luego de poblar.
     *
     * @param factory fábrica de DAOs usada para obtener acceso a la base de datos
     */
    public static void inicializarMetadata(AbstractFactory factory) {
        try {

            // 1. Clientes
            List<Cliente> clientes = CsvLoader.cargarClientes();
            ClienteDAO clienteDAO = factory.getClienteDAO();
            for (Cliente c : clientes) clienteDAO.insertar(c);
            System.out.println("✓ Clientes insertados: " + clientes.size());

            // 2. Productos
            List<Producto> productos = CsvLoader.cargarProductos();
            ProductoDAO productoDAO = factory.getProductoDAO();
            for (Producto p : productos) productoDAO.insertar(p);
            System.out.println("✓ Productos insertados: " + productos.size());

            // 3. Facturas (depende de Cliente)
            List<Factura> facturas = CsvLoader.cargarFacturas();
            FacturaDAO facturaDAO = factory.getFacturaDAO();
            for (Factura f : facturas) facturaDAO.insertar(f);
            System.out.println("✓ Facturas insertadas: " + facturas.size());

            // 4. Factura_Producto (depende de Factura y Producto)
            List<FacturaProducto> fps = CsvLoader.cargarFacturasProductos();
            FacturaProductoDAO fpDAO = factory.getFacturaProductoDAO();
            for (FacturaProducto fp : fps) fpDAO.insertar(fp);
            System.out.println("✓ Factura_Producto insertados: " + fps.size());

        } catch (IOException e) {
            System.err.println("Error al leer CSV: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al insertar en BD: " + e.getMessage());
        }
    }
}
