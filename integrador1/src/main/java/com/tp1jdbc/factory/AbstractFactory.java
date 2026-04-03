package com.tp1jdbc.factory;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;

public abstract class AbstractFactory {

        public static final int MYSQL_JDBC = 1;
        public static final int MARIA_DB_JDBC = 2;

        public abstract ClienteDAO getClienteDAO();
        public abstract FacturaDAO getFacturaDAO();
        public abstract ProductoDAO getProductoDAO();
        public abstract FacturaProductoDAO getFacturaProductoDAO();
        public abstract void closeConnection();

        public static AbstractFactory getDAOFactory(int whichFactory) {
            switch (whichFactory) {
                case MYSQL_JDBC : {
                    return MySQLDaoFactory.getInstance();
                }
                case MARIA_DB_JDBC: {
                    return MariaDbDaoFactory.getInstance();
                }
                default: return null;
            }
        }
}