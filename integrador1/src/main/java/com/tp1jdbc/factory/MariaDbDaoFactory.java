package com.tp1jdbc.factory;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbDaoFactory extends AbstractFactory {

    private static MariaDbDaoFactory instance;

    public static final String DRIVER = "org.mariadb.jdbc.Driver";
    public static final String uri = "jdbc:mariadb://localhost:3307/integrador1_db";
    public static Connection conn;

    private MariaDbDaoFactory() {
    }

    public static synchronized MariaDbDaoFactory getInstance() {
        if (instance == null) {
            instance = new MariaDbDaoFactory();
        }
        return instance;
    }

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

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAO(createConnection());
    }

    @Override
    public FacturaDAO getFacturaDAO() {
        return new FacturaDAO(createConnection());
    }

    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAO(createConnection());
    }

    @Override
    public FacturaProductoDAO getFacturaProductoDAO() {
        return new FacturaProductoDAO(createConnection());
    }
}