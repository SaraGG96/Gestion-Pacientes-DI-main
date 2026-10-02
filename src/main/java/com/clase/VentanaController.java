package com.clase;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class VentanaController {

    @FXML 
    private void salirApp() {
        Platform.exit();
    }
    
}
