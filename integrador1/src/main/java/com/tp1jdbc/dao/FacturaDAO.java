package com.tp1jdbc.dao;

import com.tp1jdbc.Conexion;
import com.tp1jdbc.entities.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief FacturaDAO
 * @details Esta clase implementa las operaciones de acceso a datos para la entidad Factura.
 * @version 1.0
 */
public class FacturaDAO implements Dao<Factura> {

    /**
     * @brief Inserta una factura en la base de datos.
     * @param f [in] factura a insertar
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
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

