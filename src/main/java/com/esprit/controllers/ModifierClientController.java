package com.esprit.controllers;

import com.esprit.models.Client;
import com.esprit.services.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ModifierClientController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private PasswordField passwordField;

    private Client client;
    private ClientService clientService = new ClientService();

    public void setClient(Client client) {
        this.client = client;
        // Populate fields with current client data
        nomField.setText(client.getNom_suser());
        prenomField.setText(client.getPrenom_user());
        emailField.setText(client.getEmail_user());
        telephoneField.setText(String.valueOf(client.getNumero_tel()));
        passwordField.setText(client.getPassword_user());
    }

    @FXML
    private void handleEnregistrer() {
        if (validateFields()) {
            // Update client object with new values
            client.setNom_suser(nomField.getText());
            client.setPrenom_user(prenomField.getText());
            client.setEmail_user(emailField.getText());
            client.setNumero_tel(Integer.parseInt(telephoneField.getText()));
            client.setPassword_user(passwordField.getText());

            // Save changes
            clientService.modifier(client);

            // Show success message
            showAlert("Succès", "Client modifié avec succès", Alert.AlertType.INFORMATION);

            // Close the window
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleAnnuler() {
        // Close the window without saving
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        // Check for empty fields
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() || telephoneField.getText().isEmpty() ||
            passwordField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return false;
        }

        // Validate name and surname (only letters and spaces)
        if (!nomField.getText().matches("^[a-zA-Z\\s]+$") || !prenomField.getText().matches("^[a-zA-Z\\s]+$")) {
            showAlert("Erreur", "Le nom et le prénom ne doivent contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        // Validate name and surname length
        if (nomField.getText().length() < 2 || nomField.getText().length() > 50) {
            showAlert("Erreur", "Le nom doit contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (prenomField.getText().length() < 2 || prenomField.getText().length() > 50) {
            showAlert("Erreur", "Le prénom doit contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Erreur", "Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate phone number
        try {
            int phoneNumber = Integer.parseInt(telephoneField.getText());
            if (phoneNumber <= 0 || String.valueOf(phoneNumber).length() < 8 || String.valueOf(phoneNumber).length() > 15) {
                showAlert("Erreur", "Le numéro de téléphone doit être un nombre valide entre 8 et 15 chiffres", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro de téléphone doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate password strength
        if (passwordField.getText().length() < 8) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (!passwordField.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins une lettre et un chiffre", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 