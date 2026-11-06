
package com.clase;

import com.clase.modelo.Cita;
import com.clase.modelo.Paciente;
import com.clase.persistencia.CitaDAOMySQL;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class CitasController implements Initializable {

    @FXML
    private TextField idCita;
    @FXML
    private TextField idDoctor;
    @FXML
    private TextField dniPaciente;
    @FXML
    private TextField motivo;
    @FXML
    private DatePicker fecha;
    @FXML
    private ComboBox<LocalTime> hora;
    @FXML
    private RadioButton pendiente;
    @FXML
    private RadioButton atendida;
    @FXML
    private RadioButton anulada;
    @FXML
    private TextArea diagnostico;
    @FXML
    private TextArea tratamiento;

    private final CitaDAOMySQL dao = new CitaDAOMySQL();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hora.setItems(FXCollections.observableArrayList(
                LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0),
                LocalTime.of(11, 0), LocalTime.of(12, 0), LocalTime.of(13, 0),
                LocalTime.of(16, 0), LocalTime.of(17, 0), LocalTime.of(18, 0)));

        ToggleGroup estados = new ToggleGroup();
        pendiente.setToggleGroup(estados);
        atendida.setToggleGroup(estados);
        anulada.setToggleGroup(estados);
        pendiente.setSelected(true);
    }

    public void cargarDatosPaciente(Paciente paciente) {
        if (paciente != null) {
            dniPaciente.setText(paciente.getDni());
        }
    }

    @FXML
    private void buscarCita() {
        try {
            Cita cita = dao.buscarCita(parseRequiredInt(idCita.getText(), "el ID de la cita"));
            if (cita == null) {
                mostrarAviso("No se encontró ninguna cita con ese ID.");
                return;
            }
            idDoctor.setText(String.valueOf(cita.getCitaiddoc()));
            dniPaciente.setText(cita.getCitadnipac());
            fecha.setValue(cita.getFechacita());
            hora.setValue(cita.getHoracita());
            motivo.setText(cita.getMotivo());
            diagnostico.setText(cita.getDiagnostico());
            tratamiento.setText(cita.getTratamiento());
            seleccionarEstado(cita.getEstadocita());
        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void guardarCita() {
        try {
            Cita cita = leerCita();
            if (idCita.getText().trim().isEmpty()) {
                dao.guardarCita(cita);
                mostrarAviso("Cita guardada correctamente.");
            } else {
                cita.setIdcita(parseRequiredInt(idCita.getText(), "el ID de la cita"));
                dao.modificarCita(cita);
                mostrarAviso("Cita modificada correctamente.");
            }
        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void eliminarCita() {
        try {
            int id = parseRequiredInt(idCita.getText(), "el ID de la cita");
            dao.eliminarCita(id);
            mostrarAviso("Cita eliminada correctamente.");
            limpiarCampos();
        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void limpiarCampos() {
        idCita.clear();
        idDoctor.clear();
        dniPaciente.clear();
        motivo.clear();
        fecha.setValue(null);
        hora.setValue(null);
        diagnostico.clear();
        tratamiento.clear();
        pendiente.setSelected(true);
    }

    private Cita leerCita() {
        if (dniPaciente.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Debes introducir el DNI del paciente.");
        }
        if (fecha.getValue() == null || hora.getValue() == null) {
            throw new IllegalArgumentException("Debes introducir la fecha y la hora.");
        }

        return new Cita(0, dniPaciente.getText().trim(),
                parseRequiredInt(idDoctor.getText(), "el ID del doctor"),
                fecha.getValue(), hora.getValue(), motivo.getText().trim(),
                diagnostico.getText().trim(), tratamiento.getText().trim(),
                estadoSeleccionado());
    }

    private int parseRequiredInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Debes introducir " + fieldName + " válido.");
        }
    }

    private String estadoSeleccionado() {
        if (atendida.isSelected()) {
            return "Atendida";
        }
        if (anulada.isSelected()) {
            return "Anulada";
        }
        return "Pendiente";
    }

    private void seleccionarEstado(String estado) {
        if ("Atendida".equalsIgnoreCase(estado)) {
            atendida.setSelected(true);
        } else if ("Anulada".equalsIgnoreCase(estado)) {
            anulada.setSelected(true);
        } else {
            pendiente.setSelected(true);
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error de base de datos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
