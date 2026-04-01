package com.tp1jdbc.dao;

import com.tp1jdbc.entities.Cliente;
import com.tp1jdbc.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief ClienteDAO
 * @details Esta clase implementa las operaciones de acceso a datos para la entidad Cliente.
 * @version 1.0
 */
public class ClienteDAO implements Dao<Cliente> {

    /**
     * @brief Inserta un cliente en la base de datos.
     * @param c [in] cliente a insertar
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO Cliente (idCliente, nombre, email) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getEmail());
            ps.executeUpdate();
        }
    }

    /**
     * @brief Lista todos los clientes almacenados en la base de datos.
     * @return lista con todos los clientes existentes
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("email")
                ));
            }
        }
        return clientes;
    }
}
