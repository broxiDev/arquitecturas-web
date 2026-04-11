package com.tp1jdbc.factory;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;

/**
 * Define la interfaz abstracta para obtener los DAOs según el motor de base de datos.
 * Usa el patrón Abstract Factory para desacoplar la creación de DAOs de su implementación concreta.
 *
 * @version 1.0
 */
public abstract class AbstractFactory {

        public static final int MYSQL_JDBC = 1;
        public static final int MARIA_DB_JDBC = 2;

        /**
         * Obtiene el DAO para la entidad Cliente.
         *
         * @return instancia de {@link ClienteDAO}
         */
        public abstract ClienteDAO getClienteDAO();

        /**
         * Obtiene el DAO para la entidad Factura.
         *
         * @return instancia de {@link FacturaDAO}
         */
        public abstract FacturaDAO getFacturaDAO();

        /**
         * Obtiene el DAO para la entidad Producto.
         *
         * @return instancia de {@link ProductoDAO}
         */
        public abstract ProductoDAO getProductoDAO();

        /**
         * Obtiene el DAO para la relación Factura_Producto.
         *
         * @return instancia de {@link FacturaProductoDAO}
         */
        public abstract FacturaProductoDAO getFacturaProductoDAO();

        /**
         * Cierra la conexión administrada por la fábrica.
         */
        public abstract void closeConnection();

        /**
         * Devuelve la fábrica correspondiente al motor de base de datos indicado.
         *
         * @param whichFactory identificador del motor de base de datos ({@link #MYSQL_JDBC} o {@link #MARIA_DB_JDBC})
         * @return instancia de {@code AbstractFactory}, o {@code null} si el identificador no existe
         */
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
