package com.esprit.controllers;

import com.esprit.models.Client;
import com.esprit.models.User;
import com.esprit.services.ClientService;
import com.esprit.services.UserService;
import com.esprit.services.RegistrationEmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.esprit.utils.DataSource;

public class InscriptionController {

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrenom;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNumeroTel;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ImageView imgPreview;

    private String selectedImagePath;
    private UserService userService = new UserService();
    private ClientService clientService = new ClientService();
    private RegistrationEmailService registrationEmailService = new RegistrationEmailService();
    private Connection connection = DataSource.getInstance().getConnection();

    @FXML
    public void initialize() {
        try {
            // Try to load default image
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default.jpg"));
            if (defaultImage != null) {
                imgPreview.setImage(defaultImage);
            }
        } catch (Exception e) {
            // If default image is not available, just leave the ImageView empty
            System.out.println("Default image not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(imgPreview.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create images directory if it doesn't exist
                Path imagesDir = Paths.get("src/main/resources/images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }

                // Generate unique filename
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = imagesDir.resolve(fileName);

                // Copy the selected file to the images directory
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Update the preview
                Image image = new Image(targetPath.toUri().toString());
                imgPreview.setImage(image);

                // Store the relative path for database
                selectedImagePath = "images/" + fileName;
            } catch (IOException e) {
                showAlert("Erreur lors du chargement de l'image: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private int getLastInsertedUserId() {
        String req = "SELECT LAST_INSERT_ID() as id";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Error getting last inserted user ID: " + e.getMessage());
        }
        return -1;
    }

    @FXML
    private void handleInscription() {
        if (!validateInputs()) {
            return;
        }

        try {
            // First, create the User
            User user = new User(
                txtNom.getText(),
                txtPrenom.getText(),
                txtEmail.getText(),
                txtPassword.getText()
            );
            userService.ajouter(user);

            // Get the ID of the newly created user
            int userId = getLastInsertedUserId();
            if (userId == -1) {
                throw new Exception("Failed to get user ID");
            }

            // Then, create the Client
            Client client = new Client(
                Integer.parseInt(txtNumeroTel.getText()),
                selectedImagePath != null ? selectedImagePath : "images/default.jpg",
                txtNom.getText(),
                txtPrenom.getText(),
                txtEmail.getText(),
                txtPassword.getText()
            );
            clientService.ajouter(client);

            // Send welcome email using the registration email service
            registrationEmailService.sendWelcomeEmail(
                txtEmail.getText(),
                txtPrenom.getText(),
                txtNom.getText()
            );

            showAlert("Inscription réussie ! Un email de bienvenue vous a été envoyé.", Alert.AlertType.INFORMATION);
            
            // Load the login view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                
                // Get the current stage
                Stage stage = (Stage) txtNom.getScene().getWindow();
                
                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Connexion");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement de la page de connexion: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur lors de l'inscription: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        // Check for empty fields
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() || 
            txtConfirmPassword.getText().isEmpty() || txtNumeroTel.getText().isEmpty()) {
            showAlert("Veuillez remplir tous les champs", Alert.AlertType.WARNING);
            return false;
        }

        // Validate name and surname (only letters and spaces)
        if (!txtNom.getText().matches("^[a-zA-Z\\s]+$") || !txtPrenom.getText().matches("^[a-zA-Z\\s]+$")) {
            showAlert("Le nom et le prénom ne doivent contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        // Validate name and surname length
        if (txtNom.getText().length() < 2 || txtNom.getText().length() > 50) {
            showAlert("Le nom doit contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (txtPrenom.getText().length() < 2 || txtPrenom.getText().length() > 50) {
            showAlert("Le prénom doit contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        // Validate email format
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate phone number
        try {
            int phoneNumber = Integer.parseInt(txtNumeroTel.getText());
            if (phoneNumber <= 0 || String.valueOf(phoneNumber).length() < 8 || String.valueOf(phoneNumber).length() > 15) {
                showAlert("Le numéro de téléphone doit être un nombre valide entre 8 et 15 chiffres", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Le numéro de téléphone doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate password strength
        if (txtPassword.getText().length() < 8) {
            showAlert("Le mot de passe doit contenir au moins 8 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (!txtPassword.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            showAlert("Le mot de passe doit contenir au moins une lettre et un chiffre", Alert.AlertType.ERROR);
            return false;
        }

        // Check if passwords match
        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            showAlert("Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void handleAnnuler() {
        // Close the registration window
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 