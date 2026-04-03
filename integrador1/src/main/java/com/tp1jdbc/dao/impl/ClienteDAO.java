package com.tp1jdbc.dao.impl;

import com.tp1jdbc.dao.Dao;
import com.tp1jdbc.entities.Cliente;

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
    private Connection con;

    public ClienteDAO(Connection con) {
        this.con = con;
    }

    /**
     * @brief Inserta un cliente en la base de datos.
     * @param c [in] cliente a insertar
     * @throws SQLException error producido durante el intento de conexion a la base de datos
     */
    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO Cliente (idCliente, nombre, email) VALUES (?, ?, ?)";
        PreparedStatement ps = null;

        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getEmail());
            ps.executeUpdate();
            System.out.println("Cliente insertado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("email")
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
        return clientes;
    }

    /**
     * @brief Obtiene el cliente con mayor facturacion.
     * @details La facturacion se calcula como la suma de cantidad vendida por valor del producto.
     * @return cliente que mas recaudo o null si no hay ventas registradas
     */
    public Cliente obtenerClienteConMayorFacturacion() throws SQLException {
        String sql = "SELECT c.idCliente, c.nombre, c.email " +
                "FROM Cliente c " +
                "INNER JOIN Factura f ON f.idCliente = c.idCliente " +
                "INNER JOIN Factura_Producto fp ON fp.idFactura = f.idFactura " +
                "GROUP BY c.idCliente, c.nombre, c.email " +
                "ORDER BY SUM(fp.cantidad * (SELECT valor FROM Producto WHERE idProducto = fp.idProducto)) DESC LIMIT 1";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("nombre"),
                    rs.getString("email")
                );
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
        return null;
    }
}
