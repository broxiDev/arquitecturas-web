package com.tp1jdbc.dao;

import com.tp1jdbc.Conexion;
import com.tp1jdbc.entities.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief ProductoDAO
 * @details Esta clase implementa las operaciones de acceso a datos para la entidad Producto.
 * @version 1.0
 */
public class ProductoDAO implements Dao<Producto> {

    /**
     * @brief Inserta un producto en la base de datos.
     * @param p [in] producto a insertar
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO Producto (idProducto, nombre, valor) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdProducto());
            ps.setString(2, p.getNombre());
            ps.setFloat(3, p.getValor());
            ps.executeUpdate();
        }
    }

    /**
     * @brief Lista todos los productos almacenados en la base de datos.
     * @return lista con todos los productos existentes
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getFloat("valor")
                ));
            }
        }
        return productos;
    }
}

