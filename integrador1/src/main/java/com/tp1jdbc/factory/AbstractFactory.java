package com.tp1jdbc.factory;

import com.tp1jdbc.dao.impl.ClienteDAO;
import com.tp1jdbc.dao.impl.FacturaDAO;
import com.tp1jdbc.dao.impl.FacturaProductoDAO;
import com.tp1jdbc.dao.impl.ProductoDAO;

/**
 * @brief AbstractFactory
 * @details Define la interfaz para obtener los DAOs segun el motor de base de datos.
 * @version 1.0
 */
public abstract class AbstractFactory {

        public static final int MYSQL_JDBC = 1;
        public static final int MARIA_DB_JDBC = 2;

        /**
         * @brief Obtiene el DAO para la entidad Cliente.
         * @return instancia de ClienteDAO
         */
        public abstract ClienteDAO getClienteDAO();

        /**
         * @brief Obtiene el DAO para la entidad Factura.
         * @return instancia de FacturaDAO
         */
        public abstract FacturaDAO getFacturaDAO();

        /**
         * @brief Obtiene el DAO para la entidad Producto.
         * @return instancia de ProductoDAO
         */
        public abstract ProductoDAO getProductoDAO();

        /**
         * @brief Obtiene el DAO para la relacion Factura_Producto.
         * @return instancia de FacturaProductoDAO
         */
        public abstract FacturaProductoDAO getFacturaProductoDAO();

        /**
         * @brief Cierra la conexion administrada por la fabrica.
         */
        public abstract void closeConnection();

        /**
         * @brief Devuelve la fabrica segun el identificador recibido.
         * @param whichFactory [in] identificador del motor de base de datos
         * @return instancia de AbstractFactory o null si el identificador no existe
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
