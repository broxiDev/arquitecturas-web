package com.tp1jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @brief Conexion
 * @details Esta clase administra la obtencion de conexiones hacia la base de datos.
 * @version 1.0
 */
public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/integrador1_db"; // URL de la base de datos
    private static final String USUARIO = "root"; // Usuario de la base de datos
    private static final String PASSWORD = ""; // Contraseña de la base de datos

    /**
     * @brief Obtiene una conexion a la base de datos.
     * @return conexion activa con la base de datos configurada
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
