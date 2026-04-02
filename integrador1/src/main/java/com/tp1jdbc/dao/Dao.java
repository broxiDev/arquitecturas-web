package com.tp1jdbc.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * @brief Dao
 * @details Interfaz que maneja las operaciones basicas de acceso a datos.
 * @version 1.0
 * @param <T> tipo de entidad gestionada por el DAO
 */
public interface Dao<T> {
    /**
     * @brief Inserta una entidad en la base de datos.
     * @param t [in] entidad a insertar
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    void insertar(T t) throws SQLException;

    /**
     * @brief Lista todas las entidades almacenadas en la base de datos.
     * @return lista con todas las entidades existentes
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    List<T> listarTodos() throws SQLException;
}
