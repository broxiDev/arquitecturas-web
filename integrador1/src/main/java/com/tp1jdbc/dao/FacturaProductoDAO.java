package com.tp1jdbc.dao;

import com.tp1jdbc.Conexion;
import com.tp1jdbc.entities.FacturaProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief FacturaProductoDAO
 * @details Esta clase implementa las operaciones de acceso a datos para la entidad FacturaProducto.
 * @version 1.0
 */
public class FacturaProductoDAO implements Dao<FacturaProducto> {

    /**
     * @brief Inserta una relacion entre factura y producto en la base de datos.
     * @param fp [in] objeto de relacion factura-producto a insertar
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
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

    /**
     * @brief Lista todas las relaciones factura-producto almacenadas en la base de datos.
     * @return lista con todas las relaciones factura-producto existentes
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
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

