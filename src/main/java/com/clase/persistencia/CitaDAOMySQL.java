package com.clase.persistencia;

import com.clase.modelo.Cita;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOMySQL {
    public List<Cita> cargarCitas() {
        String sql = "SELECT idcita, citadnipac, citaiddoc, fechacita, horacita, motivo, "
                + "diagnostico, tratamiento, estadocita FROM citas ORDER BY fechacita, horacita";
        List<Cita> citas = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                citas.add(new Cita(
                        rs.getInt("idcita"),
                        rs.getString("citadnipac"),
                        rs.getInt("citaiddoc"),
                        rs.getDate("fechacita").toLocalDate(),
                        rs.getTime("horacita").toLocalTime(),
                        rs.getString("motivo"),
                        rs.getString("diagnostico"),
                        rs.getString("tratamiento"),
                        rs.getString("estadocita")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar las citas", e);
        }

        return citas;
    }

    public Cita buscarCita(int idCita) {
        String sql = "SELECT idcita, citadnipac, citaiddoc, fechacita, horacita, motivo, "
                + "diagnostico, tratamiento, estadocita FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cita(
                            rs.getInt("idcita"),
                            rs.getString("citadnipac"),
                            rs.getInt("citaiddoc"),
                            rs.getDate("fechacita").toLocalDate(),
                            rs.getTime("horacita").toLocalTime(),
                            rs.getString("motivo"),
                            rs.getString("diagnostico"),
                            rs.getString("tratamiento"),
                            rs.getString("estadocita"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar la cita", e);
        }

        return null;
    }

    public void guardarCita(Cita cita) {
        String sql = "INSERT INTO citas "
                + "(citadnipac, citaiddoc, fechacita, horacita, motivo, diagnostico, tratamiento, estadocita) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            asignarDatos(ps, cita);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar la cita", e);
        }
    }

    public void modificarCita(Cita cita) {
        String sql = "UPDATE citas SET citadnipac = ?, citaiddoc = ?, fechacita = ?, horacita = ?, "
                + "motivo = ?, diagnostico = ?, tratamiento = ?, estadocita = ? WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            asignarDatos(ps, cita);
            ps.setInt(9, cita.getIdcita());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo modificar la cita", e);
        }
    }

    public void eliminarCita(int idCita) {
        String sql = "DELETE FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar la cita", e);
        }
    }

    private void asignarDatos(PreparedStatement ps, Cita cita) throws SQLException {
        ps.setString(1, cita.getCitadnipac());
        ps.setInt(2, cita.getCitaiddoc());
        ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
        ps.setTime(4, java.sql.Time.valueOf(cita.getHoracita()));
        setNullableString(ps, 5, cita.getMotivo());
        setNullableString(ps, 6, cita.getDiagnostico());
        setNullableString(ps, 7, cita.getTratamiento());
        ps.setString(8, cita.getEstadocita());
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }
}
