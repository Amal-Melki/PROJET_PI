package com.esprit.core;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;

public class MainAppController {
    @FXML private StackPane moduleContainer;
    private String currentModule;

    public void loadUsersModule() {
        loadModule("/views/admin/AdminDashboard.fxml");
    }

    @FXML
    public void loadSpacesModule() {
        try {
            URL fxmlLocation = getClass().getClassLoader().getResource(
                "views/espaces/GestionEspaces.fxml");
            if (fxmlLocation == null) {
                throw new IOException("Fichier FXML introuvable");
            }
            Parent view = FXMLLoader.load(fxmlLocation);
            moduleContainer.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Échec du chargement : " + e.getMessage());
            // Option: Afficher une alerte à l'utilisateur
        }
    }

    private void loadModule(String fxmlPath) {
        try {
            currentModule = fxmlPath;
            Parent view = FXMLLoader.load(
                getClass().getClassLoader().getResource(fxmlPath));
            moduleContainer.getChildren().setAll(view);
        } catch (IOException e) {
            currentModule = null;
            e.printStackTrace();
        }
    }

    public void initialize() {
        // Test d'intégration
        if (SharedData.runIntegrationTest()) {
            System.out.println("✓ Synchronisation réussie");
        } else {
            System.out.println("! Aucune nouvelle réservation trouvée");
        }
        
        // Initialisation normale...
    }
}
