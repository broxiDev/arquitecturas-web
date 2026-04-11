package com.tp1jdbc.dao.impl;

import com.tp1jdbc.dao.Dao;
import com.tp1jdbc.entities.Producto;
import com.tp1jdbc.entities.ProductoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las operaciones de acceso a datos para la entidad {@link Producto}.
 *
 * @version 1.0
 */
public class ProductoDAO implements Dao<Producto> {
    private Connection con;

    public ProductoDAO(Connection con) {
        this.con = con;
    }

    /**
     * Inserta un producto en la base de datos.
     *
     * @param p producto a insertar
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO Producto (idProducto, nombre, valor) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, p.getIdProducto());
            ps.setString(2, p.getNombre());
            ps.setFloat(3, p.getValor());
            ps.executeUpdate();
            System.out.println("Producto insertado exitosamente.");
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
     * Lista todos los productos almacenados en la base de datos.
     *
     * @return lista con todos los productos existentes
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getFloat("valor")
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
        return productos;
    }

    /**
     * Obtiene el producto con mayor recaudación.
     * La recaudación se calcula como la suma de cantidad vendida por valor del producto.
     *
     * @return producto que más recaudó, o {@code null} si no hay ventas registradas
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public Producto obtenerProductoQueMasRecaudo() throws SQLException {
        String sql = "SELECT p.idProducto, p.nombre, p.valor " +
                "FROM Producto p " +
                "INNER JOIN Factura_Producto fp ON fp.idProducto = p.idProducto " +
                "GROUP BY p.idProducto, p.nombre, p.valor " +
                "ORDER BY SUM(fp.cantidad * p.valor) DESC LIMIT 1";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                return new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getFloat("valor")
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

    /**
     * Obtiene el producto con mayor recaudación junto con el monto total recaudado.
     * La recaudación se calcula como la suma de cantidad vendida por valor del producto.
     *
     * @return {@link ProductoDTO} con nombre, valor y recaudación total,
     *         o {@code null} si no hay ventas registradas
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public ProductoDTO obtenerProductoQueMasRecaudoDTO() throws SQLException {
        String sql = "SELECT p.nombre, p.valor, SUM(fp.cantidad * p.valor) AS recaudacion " +
                "FROM Producto p " +
                "INNER JOIN Factura_Producto fp ON fp.idProducto = p.idProducto " +
                "GROUP BY p.idProducto, p.nombre, p.valor " +
                "ORDER BY recaudacion DESC LIMIT 1";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                ProductoDTO dto = new ProductoDTO(
                        rs.getString("nombre"),
                        rs.getFloat("valor"),
                        rs.getDouble("recaudacion")
                );
                System.out.println("Producto que mas recaudo: " + dto);
                return dto;
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
