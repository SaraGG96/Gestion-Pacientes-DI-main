package com.clase.persistencia;

import com.clase.modelo.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOMySQL implements DoctorDAO {

    @Override
    public void guardarDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctores "
                + "(nomedoc, apeldoc, coledoc, movildoc, emaildoc, espedoc) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, doctor.getNomedoc());
            ps.setString(2, doctor.getApeldoc());
            ps.setBoolean(3, doctor.isColegiado());
            ps.setString(4, doctor.getTlfodoc());
            ps.setString(5, doctor.getEmaildoc());
            ps.setString(6, doctor.getLocpac());
            ps.executeUpdate();
            System.out.println("Doctor guardado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al guardar el doctor: " + e.getMessage());
        }
    }

    @Override
    public List<Doctor> cargarDoctores() {
        List<Doctor> doctores = new ArrayList<>();
        String sql = "SELECT iddoc, apeldoc, nomedoc, movildoc, emaildoc, espedoc, coledoc "
                + "FROM doctores ORDER BY apeldoc, nomedoc";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doctores.add(crearDoctor(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar los doctores: " + e.getMessage());
        }
        return doctores;
    }

    @Override
    public void eliminarDoctor(String iddoc) {
        String sql = "DELETE FROM doctores WHERE iddoc = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, iddoc);
            ps.executeUpdate();
            System.out.println("Doctor eliminado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al eliminar el doctor: " + e.getMessage());
        }
    }

    @Override
    public Doctor buscarDoctor(String iddoc) {
        String sql = "SELECT iddoc, apeldoc, nomedoc, movildoc, emaildoc, espedoc, coledoc "
                + "FROM doctores WHERE iddoc = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, iddoc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearDoctor(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el doctor: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void modificarDoctor(String iddoc, Doctor doctor) {
        String sql = "UPDATE doctores SET nomedoc = ?, apeldoc = ?, coledoc = ?, "
                + "movildoc = ?, emaildoc = ?, espedoc = ? WHERE iddoc = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, doctor.getNomedoc());
            ps.setString(2, doctor.getApeldoc());
            ps.setBoolean(3, doctor.isColegiado());
            ps.setString(4, doctor.getTlfodoc());
            ps.setString(5, doctor.getEmaildoc());
            ps.setString(6, doctor.getLocpac());
            ps.setString(7, iddoc);
            ps.executeUpdate();
            System.out.println("Doctor modificado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al modificar el doctor: " + e.getMessage());
        }
    }

    private Doctor crearDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getString("iddoc"),
                rs.getString("apeldoc"),
                rs.getString("nomedoc"),
                rs.getString("movildoc"),
                rs.getString("emaildoc"),
                rs.getString("espedoc"),
                rs.getBoolean("coledoc"));
    }
}
