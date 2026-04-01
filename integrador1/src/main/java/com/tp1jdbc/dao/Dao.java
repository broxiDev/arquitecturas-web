package com.tp1jdbc.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {
    void insertar(T t) throws SQLException;
    List<T> listarTodos() throws SQLException;
}
