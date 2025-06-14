package com.esprit.core.controllers.controllers;

import com.esprit.models.Client;
import com.esprit.services.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class EditProfileController {
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private ImageView profileImageView;
    
    private Client currentClient;
    private ClientService clientService;
    private String currentImagePath;
    private Runnable onProfileUpdated;
    
    @FXML
    public void initialize() {
        clientService = new ClientService();
    }
    
    public void setCurrentClient(Client client) {
        this.currentClient = client;
        if (client != null) {
            // Populate fields with current user data
            nomField.setText(client.getNom_suser());
            prenomField.setText(client.getPrenom_user());
            telephoneField.setText(String.valueOf(client.getNumero_tel()));
            
            // Load profile image if exists
            boolean imageLoaded = false;
            if (client.getImage_path() != null && !client.getImage_path().isEmpty()) {
                String imagePath = client.getImage_path();
                try {
                    // Try to load from resources first
                    if (imagePath.startsWith("images/")) {
                        Image image = new Image(getClass().getResourceAsStream("/" + imagePath));
                        if (image != null && !image.isError()) {
                            profileImageView.setImage(image);
                            currentImagePath = imagePath;
                            imageLoaded = true;
                        }
                    }
                    // If not loaded from resources, try file system
                    if (!imageLoaded) {
                        File imageFile = imagePath.startsWith("images/") ? new File("src/main/resources/" + imagePath) : new File(imagePath);
                        if (imageFile.exists()) {
                            Image image = new Image(new FileInputStream(imageFile));
                            profileImageView.setImage(image);
                            currentImagePath = imageFile.getPath();
                            imageLoaded = true;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                }
            }
            if (!imageLoaded) {
                // Fallback: colored circle or default image
                profileImageView.setStyle("-fx-background-color: #3498db; -fx-background-radius: 75;");
            }
        }
    }
    
    public void setOnProfileUpdated(Runnable callback) {
        this.onProfileUpdated = callback;
    }
    
    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create images directory if it doesn't exist
                Path imagesDir = Paths.get("src/main/resources/images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }
                
                // Copy the selected image to the images directory
                String newImagePath = imagesDir.resolve(selectedFile.getName()).toString();
                Files.copy(selectedFile.toPath(), Paths.get(newImagePath), StandardCopyOption.REPLACE_EXISTING);
                
                // Update the image view
                Image image = new Image(new FileInputStream(newImagePath));
                profileImageView.setImage(image);
                currentImagePath = newImagePath;
                
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors du chargement de l'image: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleSave() {
        if (currentClient == null) {
            showAlert("Erreur", "Aucun utilisateur connecté", Alert.AlertType.ERROR);
            return;
        }
        
        if (!validateInputs()) {
            return;
        }
        
        try {
            // Update user data
            currentClient.setNom_suser(nomField.getText());
            currentClient.setPrenom_user(prenomField.getText());
            currentClient.setNumero_tel(Integer.parseInt(telephoneField.getText()));
            
            // Update image path if changed
            if (currentImagePath != null) {
                currentClient.setImage_path(currentImagePath);
            }
            
            // Save changes
            clientService.modifier(currentClient);
            
            // Notify that profile was updated
            if (onProfileUpdated != null) {
                onProfileUpdated.run();
            }
            
            showAlert("Succès", "Profil mis à jour avec succès", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le numéro de téléphone doit être un nombre", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour du profil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private boolean validateInputs() {
        // Check for empty fields
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || telephoneField.getText().isEmpty()) {
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

        return true;
    }
    
    @FXML
    private void handleCancel() {
        // Reset fields to current values
        if (currentClient != null) {
            nomField.setText(currentClient.getNom_suser());
            prenomField.setText(currentClient.getPrenom_user());
            telephoneField.setText(String.valueOf(currentClient.getNumero_tel()));
            
            // Reset image if exists
            if (currentClient.getImage_path() != null && !currentClient.getImage_path().isEmpty()) {
                try {
                    File imageFile = new File(currentClient.getImage_path());
                    if (imageFile.exists()) {
                        Image image = new Image(new FileInputStream(imageFile));
                        profileImageView.setImage(image);
                        currentImagePath = currentClient.getImage_path();
                    }
                } catch (IOException e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                }
            }
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 