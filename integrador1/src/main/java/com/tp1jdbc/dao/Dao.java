package com.tp1jdbc.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define las operaciones básicas de acceso a datos.
 *
 * @param <T> tipo de entidad gestionada por el DAO
 * @version 1.0
 */
public interface Dao<T> {

    /**
     * Inserta una entidad en la base de datos.
     *
     * @param t entidad a insertar
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    void insertar(T t) throws SQLException;

    /**
     * Lista todas las entidades almacenadas en la base de datos.
     *
     * @return lista con todas las entidades existentes
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    List<T> listarTodos() throws SQLException;
}
