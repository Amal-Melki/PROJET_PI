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
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() || telephoneField.getText().isEmpty() ||
            passwordField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return false;
        }

        // Validate phone number
        try {
            Integer.parseInt(telephoneField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro de téléphone doit être un nombre", Alert.AlertType.ERROR);
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