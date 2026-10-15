package com.clase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import com.clase.modelo.Doctor;
import com.clase.persistencia.DoctorDAOMySQL;
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
    private TableView<Doctor> tablaDoctores;
    @FXML
    private ComboBox<String> locpac;
    @FXML
    private RadioButton colegiadodoc, nocolegiadodoc;
    @FXML
    private TableColumn<Doctor, String> coliddoc, colapeldoc, colnomedoc, colmovildoc, colespedoc;

    boolean doctorexiste = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coliddoc.setCellValueFactory(new PropertyValueFactory<>("iddoc"));
        colapeldoc.setCellValueFactory(new PropertyValueFactory<>("apeldoc"));
        colnomedoc.setCellValueFactory(new PropertyValueFactory<>("nomedoc"));
        colmovildoc.setCellValueFactory(new PropertyValueFactory<>("tlfodoc"));
        colespedoc.setCellValueFactory(new PropertyValueFactory<>("locpac"));
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
                        cargarDoctor();
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
        doctorexiste = false;
        cargarDoctores();
    }

    @FXML
    private void guardarDoctor() {
        String id = iddoc.getText();
        String apellidos = apeldoc.getText();
        String nombre = nomedoc.getText();
        String movil = tlfodoc.getText();
        String email = emaildoc.getText();
        String especialidad = locpac.getValue();

        Doctor doctor = new Doctor(
                id,
                apellidos,
                nombre,
                movil,
                email,
                especialidad,
                colegiadodoc.isSelected()
            );

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        if (doctorexiste) {
            dao.modificarDoctor(doctor.getIddoc(), doctor);
        } else {
            dao.guardarDoctor(doctor);
        }

        doctorexiste = false;
        cargarDoctores();
    }

    @FXML
    private void cargarDoctores() {
        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        List<Doctor> doctores = dao.cargarDoctores();
        tablaDoctores.getItems().setAll(doctores);
        if (!doctorexiste) {
            iddoc.setText(String.valueOf(siguienteId(doctores)));
        }
    }

    private int siguienteId(List<Doctor> doctores) {
        int mayorId = 0;
        for (Doctor doctor : doctores) {
            try {
                mayorId = Math.max(mayorId, Integer.parseInt(doctor.getIddoc()));
            } catch (NumberFormatException e) {
            }
        }
        return mayorId + 1;
    }

    @FXML
    private void eliminarDoctor() {
        Doctor seleccionado = tablaDoctores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        dao.eliminarDoctor(seleccionado.getIddoc());

        doctorexiste = false;
        cargarDoctores();
    }

    @FXML
    private void cargarDoctor() {
        Doctor doctoselect = tablaDoctores
                .getSelectionModel()
                .getSelectedItem();

        if (doctoselect == null) {
            return;
        } else {
            doctorexiste = true;
        }

        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        Doctor doctor = dao.buscarDoctor(doctoselect.getIddoc());

        if (doctor == null) {
            return;
        }

        iddoc.setText(doctor.getIddoc());
        apeldoc.setText(doctor.getApeldoc());
        nomedoc.setText(doctor.getNomedoc());
        tlfodoc.setText(doctor.getTlfodoc());
        emaildoc.setText(doctor.getEmaildoc());
        locpac.setValue(doctor.getLocpac());
        colegiadodoc.setSelected(doctor.isColegiado());
        nocolegiadodoc.setSelected(!doctor.isColegiado());
    }

    @FXML
    private void buscaPacDni() {
        DoctorDAOMySQL dao = new DoctorDAOMySQL();
        Doctor doctor = dao.buscarDoctor(iddoc.getText());

        if (doctor == null) {
            return;
        } else {
            doctorexiste = true;
        }

        iddoc.setText(doctor.getIddoc());
        apeldoc.setText(doctor.getApeldoc());
        nomedoc.setText(doctor.getNomedoc());
        tlfodoc.setText(doctor.getTlfodoc());
        emaildoc.setText(doctor.getEmaildoc());
        locpac.setValue(doctor.getLocpac());
        colegiadodoc.setSelected(doctor.isColegiado());
        nocolegiadodoc.setSelected(!doctor.isColegiado());
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
