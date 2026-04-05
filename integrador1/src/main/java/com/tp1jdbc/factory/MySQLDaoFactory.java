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
 * @brief MySQLDaoFactory
 * @details Implementacion concreta de AbstractFactory para MySQL, usando singleton para compartir conexion.
 * @version 1.0
 */
public class MySQLDaoFactory extends AbstractFactory{

    private static MySQLDaoFactory instance = null;

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String uri = "jdbc:mysql://localhost:3306/integrador1_db";
    public static Connection conn;

    private MySQLDaoFactory() {
    }

    /**
     * @brief Devuelve la instancia de la fabrica MySQL.
     * @return instancia singleton de MySQLDaoFactory
     */
    public static synchronized MySQLDaoFactory getInstance() {
        if (instance == null) {
            instance = new MySQLDaoFactory();
        }
        return instance;
    }

    /**
     * @brief Crea la conexion a MySQL si todavia no existe.
     * @return conexion reutilizable para operaciones DAO
     */
    public static Connection createConnection() {
        if (conn != null) {
            return conn;
        }
        String driver = DRIVER;
        try {
            Class.forName(driver).getDeclaredConstructor().newInstance();
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
     * @brief Cierra la conexion activa de MySQL.
     */
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Obtiene el DAO de Cliente para MySQL.
     * @return instancia de ClienteDAO
     */
    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAO(createConnection());
    }

    /**
     * @brief Obtiene el DAO de Factura para MySQL.
     * @return instancia de FacturaDAO
     */
    @Override
    public FacturaDAO getFacturaDAO() {
        return new FacturaDAO(createConnection());
    }

    /**
     * @brief Obtiene el DAO de Producto para MySQL.
     * @return instancia de ProductoDAO
     */
    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAO(createConnection());
    }

    /**
     * @brief Obtiene el DAO de Factura_Producto para MySQL.
     * @return instancia de FacturaProductoDAO
     */
    @Override
    public FacturaProductoDAO getFacturaProductoDAO() {
        return new FacturaProductoDAO(createConnection());
    }
}
