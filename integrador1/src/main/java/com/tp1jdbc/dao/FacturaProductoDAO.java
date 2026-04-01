package com.tp1jdbc.dao;

import com.tp1jdbc.Conexion;
import com.tp1jdbc.entities.FacturaProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaProductoDAO implements Dao<FacturaProducto> {

    public void insertar(FacturaProducto fp) throws SQLException {
        String sql = "INSERT INTO Factura_Producto (idFactura, idProducto, cantidad) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fp.getIdFactura());
            ps.setInt(2, fp.getIdProducto());
            ps.setInt(3, fp.getCantidad());
            ps.executeUpdate();
        }
    }

    public List<FacturaProducto> listarTodos() throws SQLException {
        List<FacturaProducto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Factura_Producto";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new FacturaProducto(
                    rs.getInt("idFactura"),
                    rs.getInt("idProducto"),
                    rs.getInt("cantidad")
                ));
            }
        }
        return lista;
    }
}

