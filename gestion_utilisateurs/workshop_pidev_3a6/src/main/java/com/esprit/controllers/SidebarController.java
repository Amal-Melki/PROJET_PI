package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class SidebarController {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnUtilisateurs;

    @FXML
    private Button btnEvenements;

    @FXML
    private Button btnReservations;

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void handleDashboard(ActionEvent event) {
        if (mainController != null) {
            mainController.loadContent("/Dashboard.fxml");
        } else {
            showError("Erreur de navigation", "Le contrôleur principal n'est pas initialisé.");
        }
    }

    @FXML
    void handleUtilisateurs(ActionEvent event) {
        if (mainController != null) {
            mainController.loadContent("/Utilisateurs.fxml");
        } else {
            showError("Erreur de navigation", "Le contrôleur principal n'est pas initialisé.");
        }
    }

    @FXML
    void handleEvenements(ActionEvent event) {
        if (mainController != null) {
            mainController.loadContent("/Evenements.fxml");
        } else {
            showError("Erreur de navigation", "Le contrôleur principal n'est pas initialisé.");
        }
    }

    @FXML
    void handleReservations(ActionEvent event) {
        if (mainController != null) {
            mainController.loadContent("/Reservations.fxml");
        } else {
            showError("Erreur de navigation", "Le contrôleur principal n'est pas initialisé.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 