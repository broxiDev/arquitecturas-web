package com.tp1jdbc;

import com.tp1jdbc.entities.Producto;
import com.tp1jdbc.factory.AbstractFactory;
import com.tp1jdbc.utils.DataLoader;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        AbstractFactory mysqlFactory  = AbstractFactory.getDAOFactory(AbstractFactory.MYSQL_JDBC);
        AbstractFactory mariaDBFactory  = AbstractFactory.getDAOFactory(AbstractFactory.MARIA_DB_JDBC);

        poblarDB(mysqlFactory);
        poblarDB(mariaDBFactory);
        //Borrar o eliminar luego de que se haya pobaldo ambas DB.

        // 3. Producto que mas recaudo
        Producto p = mysqlFactory.getProductoDAO().obtenerProductoQueMasRecaudo();
        System.out.println("Producto que mas recaudo: " + p);

        // 4. Clientes que mas facturaron (Top 5)
        mysqlFactory.getClienteDAO().obtenerTop5ClientesPorFacturacion();
    }

    /**
     * @brief Carga los datos desde los CSVs en la base de datos indicada por la factory.
     * @param factory [in] factory que provee la conexión y los DAOs de la BD destino
     */
    private static void poblarDB(AbstractFactory factory) {
        DataLoader.inicializarMetadata(factory);
        factory.closeConnection();
    }
}