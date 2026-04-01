package com.tp1jdbc.dao;

import com.tp1jdbc.Conexion;
import com.tp1jdbc.entities.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO implements Dao<Factura> {

    public void insertar(Factura f) throws SQLException {
        String sql = "INSERT INTO Factura (idFactura, idCliente) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, f.getIdFactura());
            ps.setInt(2, f.getIdCliente());
            ps.executeUpdate();
        }
    }

    public List<Factura> listarTodos() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Factura";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                facturas.add(new Factura(
                    rs.getInt("idFactura"),
                    rs.getInt("idCliente")
                ));
            }
        }
        return facturas;
    }
}

