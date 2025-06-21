package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OptionsAvanceesController {

    @FXML
    private void handleAjouterMateriel(ActionEvent event) {
        changerScene("/AjoutMateriel.fxml");
    }

    @FXML
    private void handleAjouterReservation(ActionEvent event) {
        changerScene("/AjoutReservation.fxml");
    }

    @FXML
    private void handleListeReservations(ActionEvent event) {
        changerScene("/ModifierReservation.fxml");
    }

    @FXML
    private void handleListeMateriels(ActionEvent event) {
        changerScene("/ModifierMateriel.fxml"); // 🔥 ajout de la scène Liste des Matériels
    }

    private void changerScene(String cheminFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();
            Stage stage = new Stage(); // nouvelle fenêtre
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
