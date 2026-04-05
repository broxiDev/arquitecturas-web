package com.tp1jdbc.factory;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @brief MariaDbDaoFactory
 * @details Implementacion de AbstractFactory para MariaDB, usando singleton para compartir conexion.
 * @version 1.0
 */
public class MariaDbDaoFactory extends AbstractFactory {

    private static MariaDbDaoFactory instance;

    public static final String DRIVER = "org.mariadb.jdbc.Driver";
    public static final String uri = "jdbc:mariadb://localhost:3307/integrador1_db";
    public static Connection conn;

    private MariaDbDaoFactory() {
    }

    /**
     * @brief Devuelve la instancia de la fabrica MariaDB.
     * @return instancia singleton de MariaDbDaoFactory
     */
    public static synchronized MariaDbDaoFactory getInstance() {
        if (instance == null) {
            instance = new MariaDbDaoFactory();
        }
        return instance;
    }

    /**
     * @brief Crea la conexion a MariaDB si todavia no existe.
     * @return conexion reutilizable para operaciones DAO
     */
    public static Connection createConnection() {
        if (conn != null) {
            return conn;
        }
        try {
            Class.forName(DRIVER).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                 | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            conn = DriverManager.getConnection(uri, "root", "");
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * @brief Cierra la conexion activa de MariaDB.
     */
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Obtiene el DAO de Cliente para MariaDB.
     * @return instancia de ClienteDAO
     */
    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAO(createConnection());
    }

    /**
     * @brief Obtiene el DAO de Factura para MariaDB.
     * @return instancia de FacturaDAO
     */
    @Override
    public FacturaDAO getFacturaDAO() {
        return new FacturaDAO(createConnection());
    }

    /**
     * @brief Obtiene el DAO de Producto para MariaDB.
     * @return instancia de ProductoDAO
     */
    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAO(createConnection());
    }

    /**
     * @brief Obtiene el DAO de Factura_Producto para MariaDB.
     * @return instancia de FacturaProductoDAO
     */
    @Override
    public FacturaProductoDAO getFacturaProductoDAO() {
        return new FacturaProductoDAO(createConnection());
    }
}
