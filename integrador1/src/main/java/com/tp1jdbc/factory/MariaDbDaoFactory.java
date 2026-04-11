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
 * Implementación concreta de {@link AbstractFactory} para MariaDB.
 * Gestiona la conexión como singleton para ser reutilizada por todos los DAOs.
 *
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
     * Devuelve la instancia singleton de esta fábrica.
     *
     * @return instancia única de {@code MariaDbDaoFactory}
     */
    public static synchronized MariaDbDaoFactory getInstance() {
        if (instance == null) {
            instance = new MariaDbDaoFactory();
        }
        return instance;
    }

    /**
     * Crea y retorna la conexión a MariaDB, reutilizándola si ya existe.
     *
     * @return conexión JDBC activa
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
     * Cierra la conexión activa con MariaDB.
     */
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.Cliente} para MariaDB.
     *
     * @return instancia de {@link ClienteDAO}
     */
    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAO(createConnection());
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.Factura} para MariaDB.
     *
     * @return instancia de {@link FacturaDAO}
     */
    @Override
    public FacturaDAO getFacturaDAO() {
        return new FacturaDAO(createConnection());
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.Producto} para MariaDB.
     *
     * @return instancia de {@link ProductoDAO}
     */
    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAO(createConnection());
    }

    /**
     * Obtiene el DAO de {@link com.tp1jdbc.entities.FacturaProducto} para MariaDB.
     *
     * @return instancia de {@link FacturaProductoDAO}
     */
    @Override
    public FacturaProductoDAO getFacturaProductoDAO() {
        return new FacturaProductoDAO(createConnection());
    }
}
