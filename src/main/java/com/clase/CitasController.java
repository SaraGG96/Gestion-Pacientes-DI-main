
package com.clase;

import com.clase.modelo.Cita;
import com.clase.persistencia.CitaDAOMySQL;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML
    private TableView<Cita> tablaPacientes;
    @FXML
    private TableColumn<Cita, Integer> colidpac;
    @FXML
    private TableColumn<Cita, String> coldocpac, colespepac, colestadopac;
    @FXML
    private TableColumn<Cita, LocalDate> coldiapac;
    @FXML
    private TableColumn<Cita, LocalTime> colhorapac;

    private final CitaDAOMySQL dao = new CitaDAOMySQL();
    private boolean citaEnEdicion;
    private String dniPacienteSeleccionado;

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

        colidpac.setCellValueFactory(new PropertyValueFactory<>("idcita"));
        coldocpac.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colespepac.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        coldiapac.setCellValueFactory(new PropertyValueFactory<>("fechacita"));
        colhorapac.setCellValueFactory(new PropertyValueFactory<>("horacita"));
        colestadopac.setCellValueFactory(new PropertyValueFactory<>("estadocita"));
        cargarCitas();

        tablaPacientes.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, nuevaCita) -> {
                    if (nuevaCita != null) {
                        mostrarCita(nuevaCita);
                    }
                });
    }

    public void cargarDatosPaciente(com.clase.modelo.Paciente paciente) {
        if (paciente == null) {
            dniPacienteSeleccionado = null;
            dniPaciente.clear();
            cargarCitas();
            return;
        }

        dniPacienteSeleccionado = paciente.getDni();
        dniPaciente.setText(dniPacienteSeleccionado);
        cargarCitasPaciente(dniPacienteSeleccionado);
    }

    public void cargarDatosDoctor(Object doctor) {
        if (doctor != null) {
            try {
                idDoctor.setText(String.valueOf(
                        doctor.getClass().getMethod("getIddoc").invoke(doctor)));
            } catch (ReflectiveOperationException ex) {
                throw new IllegalArgumentException("No se pudo obtener el ID del doctor.", ex);
            }
        }
    }

    @FXML
    private void guardarCita() {
        try {
            Cita cita = leerCita();
            if (!citaEnEdicion) {
                dao.guardarCita(cita);
                mostrarAviso("Cita creada correctamente.");
            } else {
                cita.setIdcita(parseRequiredInt(idCita.getText(), "el ID de la cita"));
                dao.modificarCita(cita);
                mostrarAviso("Cita modificada correctamente.");
            }
            cargarCitasPacienteActual();
            limpiarFormulario();
            restaurarPacienteSeleccionado();
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
            limpiarFormulario();
            cargarCitasPacienteActual();
            restaurarPacienteSeleccionado();
        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void limpiarCampos() {
        dniPacienteSeleccionado = null;
        limpiarFormulario();
        cargarCitas();
    }

    private void limpiarFormulario() {
        idCita.clear();
        idDoctor.clear();
        dniPaciente.clear();
        motivo.clear();
        fecha.setValue(null);
        hora.setValue(null);
        diagnostico.clear();
        tratamiento.clear();
        pendiente.setSelected(true);
        citaEnEdicion = false;
        if (tablaPacientes != null) {
            tablaPacientes.getSelectionModel().clearSelection();
        }
    }

    private void restaurarPacienteSeleccionado() {
        if (dniPacienteSeleccionado != null) {
            dniPaciente.setText(dniPacienteSeleccionado);
        }
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

    @FXML
    private void cargarCitas() {
        try {
            tablaPacientes.getItems().setAll(dao.cargarCitas());
        } catch (IllegalStateException e) {
            tablaPacientes.getItems().clear();
            System.err.println("ERROR al cargar las citas: " + e.getMessage());
        }
    }

    private void cargarCitasPaciente(String dniPaciente) {
        try {
            tablaPacientes.getItems().setAll(dao.cargarCitasPaciente(dniPaciente));
        } catch (IllegalStateException e) {
            tablaPacientes.getItems().clear();
            System.err.println("ERROR al cargar las citas del paciente: " + e.getMessage());
        }
    }

    private void cargarCitasPacienteActual() {
        if (dniPacienteSeleccionado == null || dniPacienteSeleccionado.trim().isEmpty()) {
            cargarCitas();
        } else {
            cargarCitasPaciente(dniPacienteSeleccionado);
        }
    }

    private void mostrarCita(Cita cita) {
        citaEnEdicion = true;
        idCita.setText(String.valueOf(cita.getIdcita()));
        idDoctor.setText(String.valueOf(cita.getCitaiddoc()));
        dniPaciente.setText(cita.getCitadnipac());
        fecha.setValue(cita.getFechacita());
        hora.setValue(cita.getHoracita());
        motivo.setText(cita.getMotivo());
        diagnostico.setText(cita.getDiagnostico());
        tratamiento.setText(cita.getTratamiento());
        seleccionarEstado(cita.getEstadocita());
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
