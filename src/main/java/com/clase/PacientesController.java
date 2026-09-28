package com.clase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URL;
import java.time.LocalDate;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import com.clase.modelo.Paciente;
import com.clase.persistencia.PacienteDAOMySQL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class PacientesController implements Initializable {

    @FXML
    private TextField dnipac, apelpac, nompac, tlfpac, emailpac, dirpac, tlfopac;
    @FXML
    private DatePicker nacpac;
    @FXML 
    private ComboBox<String> cmbpac, locpac;
    @FXML 
    private Button btnguardarpac, btnmodifpac, btndelpac;
    @FXML 
    private TableView<Paciente> tablaPacientes;
    @FXML
    private TableColumn<Paciente, String> coldnipac;
    @FXML
    private TableColumn<Paciente, String> colnompac;
    @FXML
    private TableColumn<Paciente, String> colapelpac;
    @FXML
    private TableColumn<Paciente, String> colmovilpac;
    @FXML
    private TableColumn<Paciente, String> colpropac;
    @FXML
    private TableColumn<Paciente, String> colmunipac;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarProvincias();
        cmbpac.valueProperty().addListener((observable, oldValue, newValue) -> cargarMunicipios(newValue));

        dnipac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { 
                comprobarDni();
            }
        });

        nompac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && nompac.getText() != null) {
                nompac.setText(formatearNombrePropio(nompac.getText()));
            }
        });

        apelpac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && apelpac.getText() != null) {
                apelpac.setText(formatearNombrePropio(apelpac.getText()));
            }
        });

        TextField campoTlf = getCampoTelefono();
        if (campoTlf != null) {
            campoTlf.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    comprobarTelefono();
                }
            });
        }
        cargarPacientes();
    }

    private void cargarProvincias() {
        try (InputStream entrada = getClass().getResourceAsStream("/com/clase/data/provincias.json")) {
            if (entrada == null) {
                throw new IOException("No se encontró provincias.json");
            }

            JsonObject datos = JsonParser.parseReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            Provincia[] provincias = new Gson().fromJson(datos.getAsJsonArray("provincias"), Provincia[].class);

            cmbpac.getItems().setAll(Arrays.stream(provincias)
                    .map(Provincia::getNm)
                    .collect(Collectors.toList()));
        } catch (IOException | RuntimeException e) {
            cmbpac.getItems().clear();
            System.err.println("ERROR al cargar las provincias: " + e.getMessage());
        }
    }

    private void cargarMunicipios(String nombreProvincia) {
        locpac.getItems().clear();
        if (nombreProvincia == null) {
            return;
        }

        try (InputStream entradaProvincias = getClass().getResourceAsStream("/com/clase/data/provincias.json");
             InputStream entradaMunicipios = getClass().getResourceAsStream("/com/clase/data/municipio.json")) {
            if (entradaProvincias == null || entradaMunicipios == null) {
                throw new IOException("No se encontraron los archivos de datos");
            }

            Gson gson = new Gson();
            JsonObject datosProvincias = JsonParser.parseReader(
                    new InputStreamReader(entradaProvincias, StandardCharsets.UTF_8)).getAsJsonObject();
            Provincia provincia = Arrays.stream(gson.fromJson(
                            datosProvincias.getAsJsonArray("provincias"), Provincia[].class))
                    .filter(elemento -> elemento.getNm().equals(nombreProvincia))
                    .findFirst()
                    .orElse(null);

            if (provincia == null) {
                return;
            }

            JsonObject datosMunicipios = JsonParser.parseReader(
                    new InputStreamReader(entradaMunicipios, StandardCharsets.UTF_8)).getAsJsonObject();
                Municipio[] municipios = gson.fromJson(datosMunicipios.getAsJsonArray("municipios"), Municipio[].class);
            List<String> nombresMunicipios = Arrays.stream(municipios)
                    .filter(municipio -> municipio.getCodigoProvincia().equals(String.format("%02d", provincia.getId())))
                    .map(Municipio::getNm)
                    .sorted()
                    .collect(Collectors.toList());
            locpac.getItems().setAll(nombresMunicipios);
        } catch (IOException | RuntimeException e) {
            System.err.println("ERROR al cargar los municipios: " + e.getMessage());
        }
    }

    private TextField getCampoTelefono() {
        return tlfpac != null ? tlfpac : tlfopac;
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
    private void comprobarDni() {
        String dni = dnipac.getText() != null ? dnipac.getText().trim().toUpperCase() : "";
        
        if (dni.isEmpty()) {
            dnipac.setStyle("");
            return;
        }
        
        if (validarDniNie(dni)) {
            dnipac.setStyle("-fx-border-color: green; -fx-border-width: 1.5px;");
        } else {
            dnipac.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            dnipac.setText("");
        }
    }

    private boolean validarDniNie(String documento) {
        if (documento.matches("\\d{8}[A-Z]")) {
            int numero = Integer.parseInt(documento.substring(0, 8));
            char letra = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numero % 23);
            return letra == documento.charAt(8);
        }

        if (documento.matches("[XYZ]\\d{7}[A-Z]")) {
            String nie = documento
                    .replace("X", "0")
                    .replace("Y", "1")
                    .replace("Z", "2");

            int numero = Integer.parseInt(nie.substring(0, 8));
            char letra = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numero % 23);
            return letra == documento.charAt(8);
        }

        return false;
    }

    @FXML
    private void comprobarTelefono() {
        TextField campoTlf = getCampoTelefono();
        if (campoTlf == null) return;

        String tlf = campoTlf.getText() != null ? campoTlf.getText().trim() : "";

        if (tlf.isEmpty()) {
            campoTlf.setStyle("");
            return;
        }

        if (validarTelefono(tlf)) {
            campoTlf.setStyle("-fx-border-color: green; -fx-border-width: 1.5px;");
        } else {
            campoTlf.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            campoTlf.setText("");
        }
    }

    
    private boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("^[6789]\\d{8}$");
    }

    @FXML
    private void guardarPaciente() {
        if (nacpac.getValue() == null) { 
            System.out.println("Debes introducir la fecha de nacimiento"); 
            return; }

        String dni = dnipac.getText();
        String apellidos = apelpac.getText();
        String nombre = nompac.getText();
        LocalDate fechaNacimiento = nacpac.getValue();
        String movil = tlfopac.getText();
        String email = emailpac.getText();
        String direccion = dirpac.getText();
        String provincia = cmbpac.getValue();
        String municipio = locpac.getValue(); 

        Paciente paciente = new Paciente( 
         dni,
         apellidos, 
         nombre, 
         movil, 
         email, fechaNacimiento, 
         direccion,
         provincia, 
         municipio ); 

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        dao.guardarPaciente(paciente); 
        }


        @FXML 
        private void cargarPacientes() {
            PacienteDAOMySQL dao = new PacienteDAOMySQL();
            List<Paciente> pacientes = dao.cargarPacientes();
            tablaPacientes.getItems().setAll(pacientes);
        }













    }