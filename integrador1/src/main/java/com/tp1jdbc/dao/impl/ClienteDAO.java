package com.tp1jdbc.dao.impl;

import com.tp1jdbc.dao.Dao;
import com.tp1jdbc.entities.Cliente;
import com.tp1jdbc.entities.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las operaciones de acceso a datos para la entidad {@link Cliente}.
 *
 * @version 1.0
 */
public class ClienteDAO implements Dao<Cliente> {
    private static final Logger logger = LoggerFactory.getLogger(ClienteDAO.class);
    private Connection con;

    /**
     * Crea una instancia de {@code ClienteDAO} asociada a una conexión JDBC.
     *
     * @param con conexión JDBC usada para operaciones sobre Cliente
     */
    public ClienteDAO(Connection con) {
        this.con = con;
    }

    /**
     * Inserta un cliente en la base de datos.
     *
     * @param c cliente a insertar
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
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
            logger.debug("Cliente insertado: {}", c.getIdCliente());
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
     * Lista todos los clientes almacenados en la base de datos.
     *
     * @return lista con todos los clientes existentes
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
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
     * Lista todos los clientes como DTOs.
     *
     * @return lista de ClienteDTO con nombre y email
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public List<ClienteDTO> listarTodosDTO() throws SQLException {
        List<ClienteDTO> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                clientes.add(new ClienteDTO(
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
     * Obtiene y muestra los 5 clientes con mayor facturación.
     * La facturación se calcula sumando cantidad por valor de producto para cada cliente.
     *
     * @throws SQLException si ocurre un error durante la conexión a la base de datos
     */
    public void obtenerTop5ClientesPorFacturacion() throws SQLException {
        List<ClienteDTO> clientes = new ArrayList<>();
        String sql = "SELECT c.nombre, c.email " +
                "FROM Cliente c " +
                "INNER JOIN Factura f ON f.idCliente = c.idCliente " +
                "INNER JOIN Factura_Producto fp ON fp.idFactura = f.idFactura " +
                "INNER JOIN Producto p ON p.idProducto = fp.idProducto " +
                "GROUP BY c.idCliente, c.nombre, c.email " +
                "ORDER BY SUM(fp.cantidad * p.valor) DESC " +
                "LIMIT 5";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(new ClienteDTO(
                        rs.getString("nombre"),
                        rs.getString("email")
                ));
            }
            mostrarClientes(clientes);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Muestra una lista de clientes con formato de ranking numerado.
     *
     * @param clientes lista de clientes a imprimir
     */
    private void mostrarClientes(List<ClienteDTO> clientes) {
        System.out.println("=== Top 5 clientes por facturación ===");
        for (int i = 0; i < clientes.size(); i++) {
            ClienteDTO c = clientes.get(i);
            System.out.printf("%d. %s — %s%n", i + 1, c.getNombre(), c.getEmail());
        }
    }
}
