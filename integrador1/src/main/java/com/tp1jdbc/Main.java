package com.tp1jdbc;

import com.tp1jdbc.entities.Cliente;
import com.tp1jdbc.factory.AbstractFactory;
import com.tp1jdbc.utils.DataLoader;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        //AbstractFactory mysqlFactory  = AbstractFactory.getDAOFactory(AbstractFactory.MYSQL_JDBC);
        AbstractFactory mariaFactory  = AbstractFactory.getDAOFactory(AbstractFactory.MARIA_DB_JDBC);

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
