package com.tp1jdbc.dao.impl;

import com.tp1jdbc.dao.Dao;
import com.tp1jdbc.entities.FacturaProducto;
import com.tp1jdbc.entities.FacturaProductoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las operaciones de acceso a datos para la entidad {@link FacturaProducto}.
 *
 * @version 1.0
 */
public class FacturaProductoDAO implements Dao<FacturaProducto> {
    private static final Logger logger = LoggerFactory.getLogger(FacturaProductoDAO.class);
    private Connection con;

    public FacturaProductoDAO(Connection con) {
        this.con = con;
    }

    /**
     * Inserta una relación entre factura y producto en la base de datos.
     *
     * @param fp objeto de relación factura-producto a insertar
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public void insertar(FacturaProducto fp) throws SQLException {
        String sql = "INSERT INTO Factura_Producto (idFactura, idProducto, cantidad) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, fp.getIdFactura());
            ps.setInt(2, fp.getIdProducto());
            ps.setInt(3, fp.getCantidad());
            ps.executeUpdate();
            logger.debug("FacturaProducto insertado: Factura {} - Producto {}", fp.getIdFactura(), fp.getIdProducto());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lista todas las relaciones factura-producto almacenadas en la base de datos.
     *
     * @return lista con todos los DTOs de relaciones factura-producto
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public List<FacturaProductoDTO> listarTodos() throws SQLException {
        List<FacturaProductoDTO> lista = new ArrayList<>();
        String sql = "SELECT p.nombre, p.valor, (fp.cantidad * p.valor) AS recaudacion " +
                     "FROM Factura_Producto fp " +
                     "INNER JOIN Producto p ON fp.idProducto = p.idProducto";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new FacturaProductoDTO(
                        rs.getString("nombre"),
                        rs.getFloat("valor"),
                        rs.getDouble("recaudacion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}
