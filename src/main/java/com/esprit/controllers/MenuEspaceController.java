package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class MenuEspaceController
{

    public Button btnAjoutEspace;
    public Button btnListeEspaces;
    public Button btnConsulterEspaces;
    public Button btnAjouterReservation;
    public Button btnDetailsReservation;
    public Button btnMesReservations;

    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // You can add alert dialog here to notify user of the error
        }
    }

    public void ajouterEspace(ActionEvent actionEvent) {
        loadScene(actionEvent, "AjouterEspaceAdmin.fxml");
    }

    public void listeEspaces(ActionEvent actionEvent) {
        loadScene(actionEvent, "ListeEspacesAdmin.fxml");
    }

    public void mesReservations(ActionEvent actionEvent) {
        loadScene(actionEvent, "MesReservationsClient.fxml");
    }

    public void detailsReservation(ActionEvent actionEvent) {
        loadScene(actionEvent, "DetailsReservationUser.fxml");
    }

    public void ajouterReservation(ActionEvent actionEvent) {
        loadScene(actionEvent, "AjouterReservationUser.fxml");
    }

    public void consulterespaces(ActionEvent actionEvent) {
        loadScene(actionEvent, "ConsulterEspacesUser.fxml");
    }

    // You can add other navigation methods here as needed
}