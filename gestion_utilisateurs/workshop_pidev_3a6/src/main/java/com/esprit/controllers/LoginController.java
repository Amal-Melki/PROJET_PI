package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.models.Client;
import com.esprit.services.AdminService;
import com.esprit.services.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private CheckBox chkRememberMe;

    @FXML
    private Hyperlink lnkForgotPassword;

    @FXML
    private Hyperlink lnkRegister;

    private AdminService adminService = new AdminService();
    private ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        // Add any initialization code here
    }

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            // First try to authenticate as admin
            Admin admin = adminService.authenticate(email, password);
            if (admin != null) {
                openMainApplication();
                return;
            }

            // If not admin, try to authenticate as client
            Client client = clientService.authenticate(email, password);
            if (client != null) {
                openClientNavBar(client);
                return;
            }

            showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la connexion");
            e.printStackTrace();
        }
    }

    private void openClientNavBar(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientNavBar.fxml"));
            Parent root = loader.load();
            
            ClientNavBarController controller = loader.getController();
            controller.setClient(client);
            
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Espace Client");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface client");
        }
    }

    private void openMainApplication() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainLayout.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Administration");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'application");
        }
    }

    @FXML
    private void handleForgotPassword() {
        showAlert(Alert.AlertType.INFORMATION, "Information", "Fonctionnalité à venir");
    }

    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Inscription.fxml"));
            Stage stage = (Stage) lnkRegister.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le formulaire d'inscription");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 