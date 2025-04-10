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
            System.out.println("Attempting admin login for email: " + email);
            // First try to authenticate as admin
            Admin admin = adminService.authenticate(email, password);
            if (admin != null) {
                System.out.println("Admin login successful for: " + admin.getNom_suser() + " " + admin.getPrenom_user());
                openAdminDashboard(admin);
                return;
            }

            System.out.println("Attempting client login for email: " + email);
            // If not admin, try to authenticate as client
            Client client = clientService.authenticate(email, password);
            if (client != null) {
                System.out.println("Client login successful for: " + client.getNom_suser() + " " + client.getPrenom_user());
                openNavigationView(client);
                return;
            }

            System.out.println("Login failed for email: " + email);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect");
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la connexion");
        }
    }

    private void openAdminDashboard(Admin admin) {
        try {
            System.out.println("Opening admin dashboard for: " + admin.getNom_suser() + " " + admin.getPrenom_user());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainLayout.fxml"));
            Parent root = loader.load();
            
            MainLayoutController controller = loader.getController();
            controller.setCurrentAdmin(admin);
            
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard - EventHub");
            stage.show();
            
            System.out.println("Admin dashboard opened successfully");
        } catch (IOException e) {
            System.err.println("Error opening admin dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le tableau de bord administrateur");
        }
    }

    private void openNavigationView(Client client) {
        try {
            System.out.println("Opening navigation view for client: " + client.getNom_suser() + " " + client.getPrenom_user());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Navigation.fxml"));
            Parent root = loader.load();
            
            NavigationController controller = loader.getController();
            controller.setAdminMode(false);
            controller.setCurrentUser(client);
            
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("EventHub");
            stage.show();
            
            System.out.println("Navigation view opened successfully");
        } catch (IOException e) {
            System.err.println("Error opening navigation view: " + e.getMessage());
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