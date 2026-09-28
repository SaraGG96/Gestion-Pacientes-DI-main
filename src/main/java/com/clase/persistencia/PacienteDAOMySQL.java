package com.clase.persistencia;

import com.clase.modelo.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOMySQL implements PacienteDAO {

    @Override
    public void guardarPaciente(Paciente paciente) {
        String sql = "INSERT INTO pacientes"
                + "(dnipac, apelpac, nompac, movilpac, mailpac, nacimientopac, "
                + " dirpac, propac, munipac) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, paciente.getDni());
            ps.setString(2, paciente.getApellidos());
            ps.setString(3, paciente.getNombre());
            ps.setString(4, paciente.getMovil());
            ps.setString(5, paciente.getEmail());
            ps.setDate(6, java.sql.Date.valueOf(paciente.getNacimiento()));
            ps.setString(7, paciente.getDireccion());
            ps.setString(8, paciente.getProvincia());
            ps.setString(9, paciente.getMunicipio());

            ps.executeUpdate();
            System.out.println("Paciente guardado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al guardar el paciente: " + e.getMessage());
        }
    }

    @Override
    public List<Paciente> cargarPacientes() {

        List<Paciente> pacientes = new ArrayList<>();

        String sql = "SELECT dnipac, apelpac, nompac, movilpac, "
                + "propac, munipac "
                + "FROM pacientes "
                + "ORDER BY apelpac, nompac";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Paciente paciente = new Paciente(
                        rs.getString("dnipac"),
                        rs.getString("apelpac"),
                        rs.getString("nompac"), rs.getString("movilpac"), rs.getString("propac"),
                        rs.getString("munipac"));

                pacientes.add(paciente);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar los pacientes: " + e.getMessage());
        }

        //devuelve pacientes de la bd
        return pacientes;
    }

}
