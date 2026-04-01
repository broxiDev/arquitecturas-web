package com.tp1jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public void insertar(Persona p) throws SQLException {
        String sql = "INSERT INTO persona (nombre, apellido, dni, fecha_nacimiento, email, telefono) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getApellido());
                ps.setString(3, p.getDni());
                ps.setDate(4, p.getFechaNacimiento() != null ? Date.valueOf(p.getFechaNacimiento()) : null);
                ps.setString(5, p.getEmail());
                ps.setString(6, p.getTelefono());
                ps.executeUpdate();
            System.out.println("Persona insertada correctamente.");
        }
    }

    public List<Persona> listarTodos() throws SQLException {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM persona";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Persona p = new Persona();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) p.setFechaNacimiento(fecha.toLocalDate());
                p.setEmail(rs.getString("email"));
                p.setTelefono(rs.getString("telefono"));
                personas.add(p);
            }
        }
        return personas;
    }
}
