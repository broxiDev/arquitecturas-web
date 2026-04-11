package com.tp1jdbc;

import com.tp1jdbc.factory.AbstractFactory;
import com.tp1jdbc.utils.DataLoader;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        AbstractFactory mysqlFactory  = AbstractFactory.getDAOFactory(AbstractFactory.MYSQL_JDBC);
        AbstractFactory mariaDBFactory  = AbstractFactory.getDAOFactory(AbstractFactory.MARIA_DB_JDBC);

        poblarDB(mysqlFactory);
        poblarDB(mariaDBFactory);
        //Borrar o eliminar luego de que se hayan pobaldo ambas DB.

        // 3. Producto que mas recaudo
        //mysqlFactory.getProductoDAO().obtenerProductoQueMasRecaudoDTO();

        // 4. Clientes que mas facturaron (Top 5)
        //mysqlFactory.getClienteDAO().obtenerTop5ClientesPorFacturacion();
    }

    private static void poblarDB(AbstractFactory factory) {
        DataLoader.inicializarMetadata(factory);
        factory.closeConnection();
    }
}