package com.esprit.core.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;

public class MainAppController {
    @FXML private StackPane moduleContainer;
    @FXML private Button btnUsers;
    @FXML private Button btnSpaces;
    private String currentModule;

    @FXML
    public void loadUsersModule() {
        loadModule("/com/esprit/views/users/UserDashboard.fxml", "users");
    }

    @FXML 
    public void loadSpacesModule() {
        loadModule("/com/esprit/views/spaces/SpaceManagement.fxml", "spaces");
    }

    private void loadModule(String fxmlPath, String moduleName) {
        try {
            if (!moduleName.equals(currentModule)) {
                Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
                moduleContainer.getChildren().setAll(view);
                currentModule = moduleName;
                updateActiveButton(moduleName);
            }
        } catch (IOException e) {
            System.err.println("Erreur chargement module: " + e.getMessage());
        }
    }

    private void updateActiveButton(String activeModule) {
        btnUsers.getStyleClass().remove("active");
        btnSpaces.getStyleClass().remove("active");
        
        if ("users".equals(activeModule)) {
            btnUsers.getStyleClass().add("active");
        } else {
            btnSpaces.getStyleClass().add("active");
        }
    }
}
