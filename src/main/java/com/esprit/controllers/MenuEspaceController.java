package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuEspaceController {

    @FXML
    private void openAjoutEspace(ActionEvent event) {
        changerScene("/AjouterEspace.fxml", event);
    }

    @FXML
    private void openDetailsEspace(ActionEvent event) {
        changerScene("/DetailsEspace.fxml", event);
    }

    @FXML
    private void openAjoutReservation(ActionEvent event) {
        changerScene("/AjouterReservation.fxml", event);
    }

    @FXML
    private void openDetailsReservation(ActionEvent event) {
        changerScene("/DetailsReservation.fxml", event);
    }

    private void changerScene(String cheminFXML, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            System.out.println(ex.getMessage()
                    );        }
    }
}