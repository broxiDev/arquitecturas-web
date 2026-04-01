package com.tp1jdbc.dao;

import com.tp1jdbc.Conexion;
import com.tp1jdbc.entities.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements Dao<Producto> {

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

