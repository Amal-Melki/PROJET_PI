package com.esprit.controllers;

import com.esprit.models.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class SidebarController {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnUtilisateurs;

    @FXML
    private Button btnReservations;
    
    @FXML
    private Label lblAdminName;

    private MainLayoutController mainController;
    private Admin currentAdmin;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }
    
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        if (admin != null && lblAdminName != null) {
            lblAdminName.setText(admin.getNom_suser() + " " + admin.getPrenom_user());
        }
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
    void handleReservations(ActionEvent event) {
        if (mainController != null) {
            if (hasEventAccess()) {
                mainController.loadContent("/Reservations.fxml");
            } else {
                showError("Accès refusé", "Vous n'avez pas les permissions nécessaires pour accéder aux réservations.");
            }
        } else {
            showError("Erreur de navigation", "Le contrôleur principal n'est pas initialisé.");
        }
    }
    
    @FXML
    void handleDeconnexion(ActionEvent event) {
        try {
            // Load login view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Login.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Get the current stage
            javafx.stage.Stage stage = (javafx.stage.Stage) btnDashboard.getScene().getWindow();
            
            // Set the new scene
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de se déconnecter");
        }
    }

    private boolean hasEventAccess() {
        if (currentAdmin == null) {
            return false;
        }
        int role = currentAdmin.getRole();
        return role == 0 || role == 1;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 