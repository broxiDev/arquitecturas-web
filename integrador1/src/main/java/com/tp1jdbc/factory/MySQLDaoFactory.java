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
 * Implementación concreta de {@link AbstractFactory} para MySQL.
 * Gestiona la conexión como singleton para ser reutilizada por todos los DAOs.
 *
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
     * Devuelve la instancia singleton de esta fábrica.
     *
     * @return instancia única de {@code MySQLDaoFactory}
     */
    public static synchronized MySQLDaoFactory getInstance() {
        if (instance == null) {
            instance = new MySQLDaoFactory();
        }
        return instance;
    }

    /**
     * Crea y retorna la conexión a MySQL, reutilizándola si ya existe.
     *
     * @return conexión JDBC activa
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
     * Cierra la conexión activa con MySQL.
     */
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.Cliente} para MySQL.
     *
     * @return instancia de {@link ClienteDAO}
     */
    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAO(createConnection());
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.Factura} para MySQL.
     *
     * @return instancia de {@link FacturaDAO}
     */
    @Override
    public FacturaDAO getFacturaDAO() {
        return new FacturaDAO(createConnection());
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.Producto} para MySQL.
     *
     * @return instancia de {@link ProductoDAO}
     */
    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAO(createConnection());
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.FacturaProducto} para MySQL.
     *
     * @return instancia de {@link FacturaProductoDAO}
     */
    @Override
    public FacturaProductoDAO getFacturaProductoDAO() {
        return new FacturaProductoDAO(createConnection());
    }
}
