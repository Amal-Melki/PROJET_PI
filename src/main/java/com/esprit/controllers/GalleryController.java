package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.services.EspaceService;
import com.esprit.utils.SessionManager;
import com.esprit.controllers.VueGalerieController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryController {
    @FXML
    private ListView<String> espacesList;
    private List<Espace> allEspaces;

    @FXML
    public void initialize() {
        EspaceService espaceService = new EspaceService();
        allEspaces = espaceService.getAll();
        espacesList.getItems().addAll(
            allEspaces.stream()
                .map(Espace::getNom)
                .collect(Collectors.toList())
        );
        
        // Add click handler
        espacesList.setOnMouseClicked(this::handleSpaceSelection);
    }

    private void handleSpaceSelection(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selectedName = espacesList.getSelectionModel().getSelectedItem();
            if (selectedName != null) {
                Espace selectedEspace = allEspaces.stream()
                        .filter(e -> e.getNom().equals(selectedName))
                        .findFirst()
                        .orElse(null);

                System.out.println("Espace sélectionné: " + selectedEspace);
                if (selectedEspace == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Espace non trouvé");
                    return;
                }

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/VueGalerie.fxml"));
                    Parent root = loader.load();
                    VueGalerieController controller = loader.getController();
                    System.out.println("Contrôleur récupéré: " + (controller != null));
                    Platform.runLater(() -> {
                        System.out.println("Appel de focusOnEspace");
                        controller.focusOnEspace(selectedEspace);
                    });
                    
                    Stage stage = (Stage) espacesList.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue détaillée");
                    e.printStackTrace();
                }
            }
        }
    }
    @FXML
    private void handleBack() {
        espacesList.getScene().getWindow().hide();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
