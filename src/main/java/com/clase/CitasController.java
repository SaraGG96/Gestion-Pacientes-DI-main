package com.clase;

import com.clase.modelo.Cita;
import com.clase.persistencia.CitaDAOMySQL;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class CitasController implements Initializable {
    @FXML private TextField idCita;
    @FXML private TextField idDoctor;
    @FXML private TextField dniPaciente;
    @FXML private TextField motivo;
    @FXML private DatePicker fecha;
    @FXML private javafx.scene.control.ComboBox<String> hora;
    @FXML private RadioButton pendiente;
    @FXML private RadioButton atendida;
    @FXML private RadioButton anulada;
    @FXML private TextArea diagnostico;
    @FXML private TextArea tratamiento;

    private final CitaDAOMySQL citaDao = new CitaDAOMySQL();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup estados = new ToggleGroup();
        pendiente.setToggleGroup(estados);
        atendida.setToggleGroup(estados);
        anulada.setToggleGroup(estados);
        pendiente.setSelected(true);

        hora.setItems(FXCollections.observableArrayList(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00"));
    }

    @FXML
    private void buscarCita() {
        String valor = idCita.getText().trim();
        if (valor.isEmpty()) {
            mostrarError("Indica el ID de la cita", "Escribe el identificador de la cita que quieres buscar.");
            return;
        }

        try {
            int id = Integer.parseInt(valor);
            Cita cita = citaDao.buscarCita(id);
            if (cita == null) {
                mostrarError("Cita no encontrada", "No existe ninguna cita con ese ID.");
                return;
            }
            mostrarCita(cita);
        } catch (NumberFormatException e) {
            mostrarError("ID no válido", "El ID de la cita debe ser un número entero.");
        } catch (IllegalStateException e) {
            mostrarError("Error de base de datos", detalle(e));
        }
    }

    @FXML
    private void guardarCita() {
        try {
            String idTexto = idCita.getText().trim();
            int id = idTexto.isEmpty() ? 0 : Integer.parseInt(idTexto);
            Cita cita = crearCita(id);
            if (id == 0) {
                citaDao.guardarCita(cita);
            } else {
                citaDao.modificarCita(cita);
            }
            limpiarCampos();
            mostrarInformacion("Cita guardada", "Los datos de la cita se han guardado correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("ID no válido", "El ID de la cita debe ser un número entero.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError("No se pudo guardar la cita", e instanceof IllegalStateException
                    ? detalle((IllegalStateException) e) : e.getMessage());
        }
    }

    @FXML
    private void eliminarCita() {
        String valor = idCita.getText().trim();
        if (valor.isEmpty()) {
            mostrarError("Indica el ID de la cita", "Busca o escribe el ID de la cita que quieres eliminar.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            mostrarError("ID no válido", "El ID de la cita debe ser un número entero.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar cita");
        confirmacion.setHeaderText("¿Quieres eliminar la cita " + id + "?");
        if (confirmacion.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent()) {
            try {
                citaDao.eliminarCita(id);
                limpiarCampos();
                mostrarInformacion("Cita eliminada", "La cita se ha eliminado correctamente.");
            } catch (IllegalStateException e) {
                mostrarError("No se pudo eliminar la cita", detalle(e));
            }
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

    private Cita crearCita(int id) {
        if (idDoctor.getText().trim().isEmpty() || dniPaciente.getText().trim().isEmpty()
                || fecha.getValue() == null || hora.getValue() == null) {
            throw new IllegalArgumentException("Completa el ID del doctor, DNI del paciente, fecha y hora.");
        }

        int doctor;
        try {
            doctor = Integer.parseInt(idDoctor.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID del doctor debe ser un número entero.");
        }

        LocalTime horaCita;
        try {
            horaCita = LocalTime.parse(hora.getValue());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Selecciona una hora válida.");
        }

        String estado = pendiente.isSelected() ? "Pendiente"
                : atendida.isSelected() ? "Atendida" : "Anulada";
        return new Cita(id, dniPaciente.getText().trim(), doctor, fecha.getValue(), horaCita,
                motivo.getText().trim(), diagnostico.getText().trim(), tratamiento.getText().trim(), estado);
    }

    private void mostrarCita(Cita cita) {
        idCita.setText(Integer.toString(cita.getIdcita()));
        idDoctor.setText(Integer.toString(cita.getIdDoctor()));
        dniPaciente.setText(cita.getDniPaciente());
        motivo.setText(cita.getMotivo());
        fecha.setValue(cita.getFecha());
        hora.setValue(cita.getHora().toString().substring(0, 5));
        diagnostico.setText(cita.getDiagnostico());
        tratamiento.setText(cita.getTratamiento());
        pendiente.setSelected("Pendiente".equalsIgnoreCase(cita.getEstado()));
        atendida.setSelected("Atendida".equalsIgnoreCase(cita.getEstado()));
        anulada.setSelected("Anulada".equalsIgnoreCase(cita.getEstado()));
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Gestión de citas");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Gestión de citas");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private String detalle(IllegalStateException e) {
        Throwable causa = e.getCause() == null ? e : e.getCause();
        return causa.getMessage() == null ? causa.toString() : causa.getMessage();
    }
}
