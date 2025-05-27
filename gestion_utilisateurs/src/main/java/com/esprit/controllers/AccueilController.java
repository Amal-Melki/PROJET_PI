package com.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class AccueilController {
    @FXML
    private Button btnVoirEvenements;
    
    @FXML
    private void handleVoirEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvenementsFront.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) btnVoirEvenements.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 