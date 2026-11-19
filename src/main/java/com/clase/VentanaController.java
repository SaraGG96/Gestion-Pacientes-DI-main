package com.clase;

import com.clase.persistencia.ConexionMySQL;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.swing.JRViewer;
import javax.swing.SwingUtilities;

public class VentanaController {

    @FXML
    private PacientesController pacientesViewController;
    @FXML
    private DoctoresController doctoresViewController;
    @FXML
    private CitasController citasViewController;

    @FXML
    private void initialize() {
        pacientesViewController.setCitasController(citasViewController);
        doctoresViewController.setCitasController(citasViewController);
    }

    @FXML 
    private void salirApp() {
        Platform.exit();
    }

    @FXML 
    private void mostrarAcercaDe() throws IOException {
        FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/com/clase/acercade.fxml")
        );
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Acerca de");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML 
    private void abrirDir() {
        try {
            new ProcessBuilder("explorer.exe").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML 
    private void cerrarDir() {
        try {
            new ProcessBuilder("taskkill", "/F", "/IM", "explorer.exe").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generarInformePacientes() {
        try (InputStream jrxml = getClass().getResourceAsStream("/informes/pacientes.jrxml");
                Connection conexion = ConexionMySQL.getConexion();
                InputStream logo = getClass().getResourceAsStream("/img/SANIDAD.png")) {

            if (jrxml == null) {
                throw new IOException("No se encontró el informe pacientes.jrxml");
            }

            JasperReport reporte = JasperCompileManager.compileReport(jrxml);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("LOGO", logo);

            JasperPrint informe = JasperFillManager.fillReport(reporte, parametros, conexion);
            mostrarVistaPrevia(informe);
        } catch (SQLException | IOException | net.sf.jasperreports.engine.JRException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al generar el informe",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarVistaPrevia(JasperPrint informe) {
        SwingNode visor = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JRViewer viewer = new JRViewer(informe);
            viewer.setFitWidthZoomRatio();
            visor.setContent(viewer);
        });

        Button btnDescargar = new Button("Descargar PDF");
        ToolBar barraHerramientas = new ToolBar(btnDescargar);
        BorderPane contenido = new BorderPane();
        contenido.setTop(barraHerramientas);
        contenido.setCenter(visor);

        Stage ventana = new Stage();
        ventana.setTitle("Vista previa - Informe de pacientes");
        ventana.setScene(new Scene(contenido, 900, 700));
        btnDescargar.setOnAction(event -> descargarInforme(informe, ventana));
        ventana.show();
    }

    private void descargarInforme(JasperPrint informe, Stage ventanaPrevia) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar informe de pacientes");
        selector.setInitialFileName("pacientes.pdf");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        java.io.File archivo = selector.showSaveDialog(ventanaPrevia);

        if (archivo == null) {
            return;
        }

        try {
            if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
                archivo = new java.io.File(archivo.getAbsolutePath() + ".pdf");
            }
            JasperExportManager.exportReportToPdfFile(informe, archivo.getAbsolutePath());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Informe descargado",
                    "El PDF se ha guardado en:\n" + archivo.getAbsolutePath());
        } catch (net.sf.jasperreports.engine.JRException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al descargar el informe",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
}
