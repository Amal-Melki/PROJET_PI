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
    private Button btnEvenements;
    
    @FXML
    private Button btnGestionEvenements;
    
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
        // Show/hide admin-specific buttons
        btnGestionEvenements.setVisible(isAdmin);
        btnGestionEvenements.setManaged(isAdmin);
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
        if (currentClient != null) {
            userNameLabel.setText(currentClient.getNom_suser() + " " + currentClient.getPrenom_user());
            
            // Try to load the image
            String imagePath = currentClient.getImage_path();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    // First try to load as a resource (for default images)
                    if (imagePath.startsWith("images/")) {
                        Image image = new Image(getClass().getResourceAsStream("/" + imagePath));
                        if (image != null && !image.isError()) {
                            userImageView.setImage(image);
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error loading client image from resources: " + e.getMessage());
                }
                
                try {
                    // If resource loading fails, try to load as a file
                    File imageFile;
                    if (imagePath.startsWith("images/")) {
                        // If it's a relative path, look in the resources directory
                        imageFile = new File("src/main/resources/" + imagePath);
                    } else {
                        // If it's an absolute path, use it directly
                        imageFile = new File(imagePath);
                    }
                    
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        if (image != null && !image.isError()) {
                            userImageView.setImage(image);
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error loading client image from file system: " + e.getMessage());
                }
            }
            
            // If all attempts fail, load default avatar
            loadDefaultAvatar();
        } else if (currentAdmin != null) {
            userNameLabel.setText(currentAdmin.getNom_suser() + " " + currentAdmin.getPrenom_user());
            loadDefaultAvatar();
        }
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
    public void handleEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvenementsFront.fxml"));
            Parent view = loader.load();
            
            // Get the controller and set the current client
            EvenementsFrontController controller = loader.getController();
            if (!isAdmin && currentClient != null) {
                controller.setCurrentClient(currentClient);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            updateButtonStyles(btnEvenements);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleGestionEvenements() {
        loadView("/Evenements.fxml");
        updateButtonStyles(btnGestionEvenements);
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
        btnEvenements.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        btnGestionEvenements.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
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