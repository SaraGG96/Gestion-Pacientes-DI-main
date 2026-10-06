package com.clase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import com.clase.modelo.Paciente;
import com.clase.persistencia.PacienteDAOMySQL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class DoctoresController implements Initializable {

    @FXML
    private TextField iddoc, apeldoc, nomedoc, tlfodoc, emaildoc;
    @FXML
    private Button btnbuscardoc, btnguardardoc, btndeldoc, btnreset;
    @FXML
    private TableView<Paciente> tablaDoctores;
    @FXML
    private ComboBox<String> locpac;
    @FXML
    private RadioButton colegiadodoc, nocolegiadodoc;
    @FXML
    private TableColumn<Paciente, String> coliddoc, colapeldoc, colnomedoc, colmovildoc, colespedoc;

    boolean pacienteexiste = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coliddoc.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colapeldoc.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colnomedoc.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colmovildoc.setCellValueFactory(new PropertyValueFactory<>("movil"));
        colespedoc.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        cargarEspecialidades();

        nomedoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && nomedoc.getText() != null) {
                nomedoc.setText(formatearNombrePropio(nomedoc.getText()));
            }
        });

        apeldoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && apeldoc.getText() != null) {
                apeldoc.setText(formatearNombrePropio(apeldoc.getText()));
            }
        });

        tlfodoc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                comprobarTelefono();
            }
        });
        cargarDoctores();

        tablaDoctores.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> {

                    if (nuevo != null) {
                        cargarPaciente();
                    }
                });

    }

    private String formatearNombrePropio(String texto) {
        texto = texto.trim();
        if (texto.isEmpty()) {
            return "";
        }

        String[] palabras = texto.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    @FXML
    private void comprobarTelefono() {
        String tlf = tlfodoc.getText() != null ? tlfodoc.getText().trim() : "";

        if (tlf.isEmpty()) {
            tlfodoc.setStyle("");
            return;
        }

        if (validarTelefono(tlf)) {
            tlfodoc.setStyle("-fx-border-color: green; -fx-border-width: 1.5px;");
        } else {
            tlfodoc.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            tlfodoc.setText("");
        }
    }

    private boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("^[6789]\\d{8}$");
    }

    @FXML
    private void resetCampos() {
        iddoc.clear();
        apeldoc.clear();
        nomedoc.clear();
        tlfodoc.clear();
        emaildoc.clear();
        locpac.getSelectionModel().clearSelection();
        colegiadodoc.setSelected(false);
        nocolegiadodoc.setSelected(false);
        tlfodoc.setStyle("");
        pacienteexiste = false;
        cargarDoctores();
    }

    @FXML
    private void guardarPaciente() {
        String dni = iddoc.getText();
        String apellidos = apeldoc.getText();
        String nombre = nomedoc.getText();
        String movil = tlfodoc.getText();
        String email = emaildoc.getText();
        String especialidad = locpac.getValue();

        Paciente paciente = new Paciente(
                dni,
                apellidos,
                nombre,
                movil,
                email,
                especialidad
            );

        if (pacienteexiste) {
            PacienteDAOMySQL dao = new PacienteDAOMySQL();
            dao.modificarPaciente(paciente.getDni(), paciente);
            pacienteexiste = false;
        } else {
            PacienteDAOMySQL dao = new PacienteDAOMySQL();
            dao.guardarPaciente(paciente);
        }

        pacienteexiste = false;
        cargarDoctores();
    }

    @FXML
    private void cargarDoctores() {
        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        List<Paciente> doctores = dao.cargarDoctores();
        tablaDoctores.getItems().setAll(doctores);
        if (!pacienteexiste) {
            iddoc.setText(String.valueOf(siguienteId(doctores)));
        }
    }

    private int siguienteId(List<Paciente> doctores) {
        int mayorId = 0;
        for (Paciente doctor : doctores) {
            try {
                mayorId = Math.max(mayorId, Integer.parseInt(doctor.getDni()));
            } catch (NumberFormatException e) {
                // Los registros antiguos con DNI no participan en la numeración.
            }
        }
        return mayorId + 1;
    }

    @FXML
    private void eliminarDoctor() {
        Paciente seleccionado = tablaDoctores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        dao.eliminarPaciente(seleccionado.getDni());

        pacienteexiste = false;
        cargarDoctores();
    }

    @FXML
    private void cargarPaciente() {
        Paciente pacienteselect = tablaDoctores
                .getSelectionModel()
                .getSelectedItem();

        if (pacienteselect == null) {
            return;
        } else {
            pacienteexiste = true;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        Paciente paciente = dao.buscarPaciente(pacienteselect.getDni());

        if (paciente == null) {
            return;
        }

        iddoc.setText(paciente.getDni());
        apeldoc.setText(paciente.getApellidos());
        nomedoc.setText(paciente.getNombre());
        tlfodoc.setText(paciente.getMovil());
        emaildoc.setText(paciente.getEmail());
        locpac.setValue(paciente.getProvincia());
    }

    @FXML
    private void buscaPacDni() {
        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        Paciente paciente = dao.buscaPacDni(iddoc.getText());

        if (paciente == null) {
            return;
        } else {
            pacienteexiste = true;
        }

        iddoc.setText(paciente.getDni());
        apeldoc.setText(paciente.getApellidos());
        nomedoc.setText(paciente.getNombre());
        tlfodoc.setText(paciente.getMovil());
        emaildoc.setText(paciente.getEmail());
        locpac.setValue(paciente.getProvincia());
    }

    @FXML 
    private void cargarEspecialidades() {
        locpac.getItems().clear();
        locpac.getItems().addAll(
                "Alergología",
                "Anestesiología y Reanimación",
                "Angiología y Cirugía Vascular",
                "Cardiología",
                "Cirugía Cardiovascular",
                "Cirugía General y del Aparato Digestivo",
                "Cirugía Oral y Maxilofacial",
                "Cirugía Ortopédica y Traumatología",
                "Cirugía Pediátrica",
                "Cirugía Plástica, Estética y Reparadora",
                "Dermatología Médico-Quirúrgica y Venereología",
                "Endocrinología y Nutrición",
                "Gastroenterología y Hepatología",
                "Genética Clínica",
                "Geriatría",
                "Ginecología y Obstetricia",
                "Hematología y Hemoterapia",
                "Inmunología",
                "Medicina del Trabajo",
                "Medicina Familiar y Comunitaria",
                "Medicina Intensiva",
                "Medicina Interna",
                "Medicina Legal y Forense",
                "Medicina Nuclear",
                "Medicina Preventiva y Salud Pública",
                "Medicina Física y Rehabilitación",
                "Microbiología y Parasitología Clínica",
                "Nefrología",
                "Neumología",
                "Neurocirugía",
                "Neurología",
                "Obstetricia y Ginecología (MIR)",
                "Oftalmología",
                "Oncología Médica",
                "Oncología Radioterápica",
                "Otorrinolaringología (ORL)",
                "Pediatría y Áreas Específicas de la Pediatría (MIR)",
                "Psiquiatría (MIR)",
                "Radiodiagnóstico (MIR)",
                "Reumatología (MIR)",
                "Urología (MIR)"
        );
    }
}
