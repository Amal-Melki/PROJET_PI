package com.esprit.controllers;

import com.esprit.models.Admin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.Scene;

import java.io.IOException;

public class MainLayoutController {
    
    @FXML
    private StackPane contentArea;

    @FXML
    private VBox sidebarContainer;
    
    private Admin currentAdmin;
    private SidebarController sidebarController;

    @FXML
    public void initialize() {
        try {
            // Load the sidebar
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/Sidebar.fxml"));
            Parent sidebar = sidebarLoader.load();
            
            // Get the sidebar controller and set this as the main controller
            sidebarController = sidebarLoader.getController();
            sidebarController.setMainController(this);
            
            // Add the sidebar to the container
            sidebarContainer.getChildren().add(sidebar);
            
            // Load initial content (e.g., dashboard)
            loadContent("/Dashboard.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur d'initialisation", "Impossible de charger l'interface.");
        }
    }
    
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        if (sidebarController != null) {
            sidebarController.setCurrentAdmin(admin);
        }
    }
    
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            
            // Get the controller and check if it implements AdminAware
            Object controller = loader.getController();
            if (controller instanceof AdminAware) {
                ((AdminAware) controller).setAdmin(currentAdmin);
            }
            
            // Add the CSS file to the scene
            Scene scene = content.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass().getResource("/styles/tables.css").toExternalForm());
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur de chargement", "Impossible de charger la vue: " + fxmlPath);
        }
    }

    private void showError(String title, String message) {
        VBox errorBox = new VBox(10);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ff7fa8;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        errorBox.getChildren().addAll(titleLabel, messageLabel);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(errorBox);
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 