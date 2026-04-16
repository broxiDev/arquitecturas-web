package com.tp1jdbc.dao.impl;

import com.tp1jdbc.dao.Dao;
import com.tp1jdbc.entities.Factura;
import com.tp1jdbc.entities.FacturaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las operaciones de acceso a datos para la entidad {@link Factura}.
 *
 * @version 1.0
 */
public class FacturaDAO implements Dao<Factura> {
    private static final Logger logger = LoggerFactory.getLogger(FacturaDAO.class);
    private Connection con;

    public FacturaDAO(Connection con) {
        this.con = con;
    }

    /**
     * Inserta una factura en la base de datos.
     *
     * @param f factura a insertar
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public void insertar(Factura f) throws SQLException {
        String sql = "INSERT INTO Factura (idFactura, idCliente) VALUES (?, ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, f.getIdFactura());
            ps.setInt(2, f.getIdCliente());
            ps.executeUpdate();
            logger.debug("Factura insertada: {}", f.getIdFactura());
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
     * Lista todas las facturas almacenadas en la base de datos.
     *
     * @return lista con todos los DTOs de facturas existentes
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public List<Factura> listarTodos() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Factura";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                facturas.add(new Factura(
                        rs.getInt("idFactura"),
                        rs.getInt("idCliente")
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
        return facturas;
    }

    /**
     * Lista todas las facturas como DTOs (con nombre de cliente).
     *
     * @return lista de FacturaDTO
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public List<FacturaDTO> listarTodosDTO() throws SQLException {
        List<FacturaDTO> facturas = new ArrayList<>();
        String sql = "SELECT f.idFactura, c.nombre " +
                     "FROM Factura f " +
                     "INNER JOIN Cliente c ON f.idCliente = c.idCliente";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                facturas.add(new FacturaDTO(
                        rs.getInt("idFactura"),
                        rs.getString("nombre")
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
        return facturas;
    }
}
