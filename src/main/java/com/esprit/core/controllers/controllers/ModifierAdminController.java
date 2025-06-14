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

public class ModifierAdminController implements Initializable {

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
    private Admin adminToModify;

    public void setAdmin(Admin admin) {
        this.adminToModify = admin;
        // Fill the form with admin data
        txtNom.setText(admin.getNom_suser());
        txtPrenom.setText(admin.getPrenom_user());
        txtEmail.setText(admin.getEmail_user());
        txtPassword.setText(admin.getPassword_user());
        
        // Set the role in ComboBox
        switch (admin.getRole()) {
            case 1:
                comboRole.setValue("Organisateur");
                break;
            case 2:
                comboRole.setValue("Fournisseur");
                break;
            case 3:
                comboRole.setValue("Moderateur");
                break;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the role ComboBox
        comboRole.setItems(FXCollections.observableArrayList(
            "Organisateur",
            "Fournisseur",
            "Moderateur"
        ));
    }

    @FXML
    void handleModifier() {
        try {
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

            // Update admin data
            adminToModify.setNom_suser(txtNom.getText());
            adminToModify.setPrenom_user(txtPrenom.getText());
            adminToModify.setEmail_user(txtEmail.getText());
            adminToModify.setPassword_user(txtPassword.getText());
            adminToModify.setRole(roleValue);

            // Save to database
            adminService.modifier(adminToModify);

            // Show success message
            showAlert("Succès", "Administrateur modifié avec succès", Alert.AlertType.INFORMATION);

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

        // Validate name and surname length
        if (txtNom.getText().length() < 2 || txtNom.getText().length() > 50) {
            showAlert("Erreur", "Le nom doit contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (txtPrenom.getText().length() < 2 || txtPrenom.getText().length() > 50) {
            showAlert("Erreur", "Le prénom doit contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        // Validate email format
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Erreur", "Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate password strength
        if (txtPassword.getText().length() < 8) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (!txtPassword.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins une lettre et un chiffre", Alert.AlertType.ERROR);
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