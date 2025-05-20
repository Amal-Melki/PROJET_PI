package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.models.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.Alert;
import com.esprit.services.ClientService;

public class NavigationController {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Button btnAccueil;
    
    @FXML
    private Button btnDeconnexion;
    
    @FXML
    private HBox userInfoBox;
    
    @FXML
    private ImageView userImageView;
    
    @FXML
    private Label userNameLabel;
    
    private boolean isAdmin;
    private Client currentClient;
    private Admin currentAdmin;
    
    public void setAdminMode(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    public void setCurrentUser(Client client) {
        this.currentClient = client;
        updateUserInfo();
    }
    
    public void setCurrentUser(Admin admin) {
        this.currentAdmin = admin;
        updateUserInfo();
    }
    
    private void updateUserInfo() {
        if (isAdmin && currentAdmin != null) {
            userNameLabel.setText("Admin: " + currentAdmin.getNom_suser() + " " + currentAdmin.getPrenom_user());
            // Set default admin image
            try {
                Image adminImage = new Image(getClass().getResourceAsStream("/images/admin-avatar.png"));
                if (adminImage != null && !adminImage.isError()) {
                    userImageView.setImage(adminImage);
                } else {
                    loadDefaultAvatar();
                }
            } catch (Exception e) {
                System.err.println("Error loading admin avatar: " + e.getMessage());
                loadDefaultAvatar();
            }
        } else if (!isAdmin && currentClient != null) {
            userNameLabel.setText(currentClient.getNom_suser() + " " + currentClient.getPrenom_user());
            // Always try to load the client image if available
            boolean imageLoaded = false;
            if (currentClient.getImage_path() != null && !currentClient.getImage_path().isEmpty()) {
                String imagePath = currentClient.getImage_path();
                try {
                    // Try to load from resources first
                    Image clientImage = null;
                    if (imagePath.startsWith("images/")) {
                        clientImage = new Image(getClass().getResourceAsStream("/" + imagePath));
                        if (clientImage != null && !clientImage.isError()) {
                            userImageView.setImage(clientImage);
                            imageLoaded = true;
                        }
                    }
                    // If not loaded from resources, try file system
                    if (!imageLoaded) {
                        File imageFile = imagePath.startsWith("images/") ? new File("src/main/resources/" + imagePath) : new File(imagePath);
                        if (imageFile.exists()) {
                            clientImage = new Image(imageFile.toURI().toString());
                            if (clientImage != null && !clientImage.isError()) {
                                userImageView.setImage(clientImage);
                                imageLoaded = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading client image: " + e.getMessage());
                }
            }
            if (!imageLoaded) {
                loadDefaultAvatar();
            }
        }
        // Make user info visible
        userInfoBox.setVisible(true);
        userInfoBox.setManaged(true);
    }
    
    private void loadDefaultAvatar() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-avatar.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                userImageView.setImage(defaultImage);
            } else {
                // Create a simple colored circle as fallback
                userImageView.setStyle("-fx-background-color: #3498db; -fx-background-radius: 20;");
            }
        } catch (Exception e) {
            // Create a simple colored circle as fallback
            userImageView.setStyle("-fx-background-color: #3498db; -fx-background-radius: 20;");
            System.out.println("Using fallback avatar style");
        }
    }
    
    @FXML
    public void initialize() {
        // Load Accueil view by default
        handleAccueil();
        
        // Configure user image view
        userImageView.setFitHeight(40);
        userImageView.setFitWidth(40);
        userImageView.setPreserveRatio(true);
        userImageView.setStyle("-fx-background-radius: 20; -fx-clip-path: circle;");
        
        // Set initial default avatar
        loadDefaultAvatar();
    }
    
    @FXML
    private void handleAccueil() {
        loadView("/Accueil.fxml");
        updateButtonStyles(btnAccueil);
    }
    
    @FXML
    private void handleDeconnexion() {
        try {
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditerProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent editProfileView = loader.load();
            
            EditProfileController controller = loader.getController();
            controller.setCurrentClient(currentClient);
            
            // Add a listener to refresh user info when profile is updated
            controller.setOnProfileUpdated(() -> {
                // Refresh the current client data
                if (!isAdmin && currentClient != null) {
                    // Reload the client data from the database
                    ClientService clientService = new ClientService();
                    Client updatedClient = clientService.rechercherParId(currentClient.getId_user());
                    if (updatedClient != null) {
                        currentClient = updatedClient;
                        updateUserInfo();
                    }
                }
            });
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(editProfileView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de modification du profil", Alert.AlertType.ERROR);
        }
    }
    
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateButtonStyles(Button activeButton) {
        // Reset all buttons to default style
        btnAccueil.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Set active button style
        activeButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;");
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 