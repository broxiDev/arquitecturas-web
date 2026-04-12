package com.tp1jdbc.utils;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;
import com.tp1jdbc.factory.AbstractFactory;
import com.tp1jdbc.utils.csv.CsvLoader;
import com.tp1jdbc.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    /**
     * @brief Lee los 4 CSVs y los persiste en la base de datos.
     * @details El orden respeta las FK: Cliente -> Producto -> Factura -> Factura_Producto.
     * Los logs detallados por inserción están en nivel DEBUG (no se muestran por defecto).
     * Se muestra un resumen al final con totales y errores.
     * IMPORTANTE: ejecutar solo 1 vez, luego comentar la llamada en Main.
     * @param factory [in] fabrica de DAOs usada para obtener acceso a la base de datos
     */
    public static void inicializarMetadata(AbstractFactory factory) {
        logger.info("=== Iniciando carga de datos desde CSV ===");
        
        try {
            // 1. Clientes
            logger.info("Cargando clientes...");
            List<Cliente> clientes = CsvLoader.cargarClientes();
            ClienteDAO clienteDAO = factory.getClienteDAO();
            int clientesInsertados = insertarBloque(clienteDAO, clientes, "Cliente");
            
            // 2. Productos
            logger.info("Cargando productos...");
            List<Producto> productos = CsvLoader.cargarProductos();
            ProductoDAO productoDAO = factory.getProductoDAO();
            int productosInsertados = insertarBloque(productoDAO, productos, "Producto");
            
            // 3. Facturas (depende de Cliente)
            logger.info("Cargando facturas...");
            List<Factura> facturas = CsvLoader.cargarFacturas();
            FacturaDAO facturaDAO = factory.getFacturaDAO();
            int facturasInsertadas = insertarBloque(facturaDAO, facturas, "Factura");
            
            // 4. Factura_Producto (depende de Factura y Producto)
            logger.info("Cargando relaciones factura-producto...");
            List<FacturaProducto> fps = CsvLoader.cargarFacturasProductos();
            FacturaProductoDAO fpDAO = factory.getFacturaProductoDAO();
            int fpsInsertados = insertarBloque(fpDAO, fps, "FacturaProducto");
            
            // Resumen final
            logger.info("=== Resumen de carga ===");
            logger.info("✓ Clientes insertados: {}", clientesInsertados);
            logger.info("✓ Productos insertados: {}", productosInsertados);
            logger.info("✓ Facturas insertadas: {}", facturasInsertadas);
            logger.info("✓ Relaciones Factura-Producto insertadas: {}", fpsInsertados);
            logger.info("=== Carga completada exitosamente ===");

        } catch (IOException e) {
            logger.error("Error al leer CSV: {}", e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Error al insertar en BD: {}", e.getMessage(), e);
        }
    }
    
    /**
     * @brief Inserta un bloque de entidades manteniendo silencio sobre registros individuales.
     * @param dao [in] el DAO genérico a usar
     * @param lista [in] lista de entidades a insertar
     * @param nombreEntidad [in] nombre de la entidad para logging
     * @return cantidad de registros insertados exitosamente
     */
    private static <T> int insertarBloque(com.tp1jdbc.dao.Dao<T> dao, List<T> lista, String nombreEntidad) {
        int contador = 0;
        for (T entidad : lista) {
            try {
                dao.insertar(entidad);
                contador++;
            } catch (SQLException e) {
                logger.warn("Error al insertar {}: {}", nombreEntidad, e.getMessage());
            }
        }
        return contador;
    }
}
