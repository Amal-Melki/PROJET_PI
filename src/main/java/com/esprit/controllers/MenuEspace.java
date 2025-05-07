package com.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuEspace {

    @FXML
    private Button btnAjoutEspace;

    @FXML
    private Button btnListeEspaces;

    @FXML
    private Button btnConsulterEspaces;

    @FXML
    private Button btnAjouterReservation;

    @FXML
    private Button btnMesReservations;

    @FXML
    private Button btnDetailsReservation;

    @FXML
    public void initialize() {
        // Configurer les actions des boutons
        btnAjoutEspace.setOnAction(this::navigateToAjoutEspace);
        btnListeEspaces.setOnAction(this::navigateToListeEspaces);
        btnConsulterEspaces.setOnAction(this::navigateToConsulterEspaces);
        btnMesReservations.setOnAction(this::navigateToMesReservations);
        btnDetailsReservation.setOnAction(this::navigateToDetailsReservation);
    }

    @FXML
    private void navigateToAjoutEspace(ActionEvent event) {
        navigateTo(event, "/com/esprit/views/AjoutEspace.fxml");
    }

    @FXML
    private void navigateToListeEspaces(ActionEvent event) {
        navigateTo(event, "/com/esprit/views/ListeEspacesAdmin.fxml");
    }

    @FXML
    private void navigateToConsulterEspaces(ActionEvent event) {
        navigateTo(event, "/com/esprit/views/ConsulterEspacesClient.fxml");
    }

    @FXML
    private void navigateToMesReservations(ActionEvent event) {
        navigateTo(event, "/com/esprit/views/MesReservationsClient.fxml");
    }

    @FXML
    private void navigateToDetailsReservation(ActionEvent event) {
        navigateTo(event, "/com/esprit/views/DetailsReservationClient.fxml");
    }

    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
