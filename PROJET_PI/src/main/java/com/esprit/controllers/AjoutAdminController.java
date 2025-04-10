package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.services.AdminService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
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
            "Hôte"
        ));
        comboRole.getSelectionModel().selectFirst(); // Select first item by default
    }

    @FXML
    void handleAjouter() {
        try {
            // Validate inputs
            if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || 
                txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() ||
                comboRole.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
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
                case "Hôte":
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