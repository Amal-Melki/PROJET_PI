package com.esprit.core.controllers.controllers;

import com.esprit.services.AdminService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AjoutAdminController implements Initializable {

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrenom;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> comboRole;

    private AdminService adminService = new AdminService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the role ComboBox
        comboRole.setItems(FXCollections.observableArrayList(
            "Organisateur",
            "Fournisseur",
            "Moderateur"
        ));
        comboRole.getSelectionModel().selectFirst(); // Select first item by default
    }

    @FXML
    void handleAjouter() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Get role value based on selection
            int roleValue;
            switch (comboRole.getValue()) {
                case "Organisateur":
                    roleValue = 1;
                    break;
                case "Fournisseur":
                    roleValue = 2;
                    break;
                case "Moderateur":
                    roleValue = 3;
                    break;
                default:
                    roleValue = 1; // Default to Organisateur
            }

            // Create new admin with role
            Admin admin = new Admin(roleValue, txtNom.getText(), txtPrenom.getText(), txtEmail.getText(), txtPassword.getText());

            // Save to database
            adminService.ajouter(admin);

            // Show success message
            showAlert("Succès", "Administrateur ajouté avec succès", Alert.AlertType.INFORMATION);

            // Close the window
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        // Check for empty fields
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() ||
            comboRole.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return false;
        }

        // Validate name and surname (only letters and spaces)
        if (!txtNom.getText().matches("^[a-zA-Z\\s]+$") || !txtPrenom.getText().matches("^[a-zA-Z\\s]+$")) {
            showAlert("Erreur", "Le nom et le prénom ne doivent contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        // Validate email format
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Erreur", "Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate password (minimum 8 characters, at least one number and one letter)
        if (!txtPassword.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères, une lettre et un chiffre", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    void handleAnnuler() {
        // Close the window
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 