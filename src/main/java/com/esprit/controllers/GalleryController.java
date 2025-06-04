package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.services.EspaceService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.stream.Collectors;

public class GalleryController {
    @FXML
    private ListView<String> espacesList;

    @FXML
    public void initialize() {
        EspaceService espaceService = new EspaceService();
        espacesList.getItems().addAll(
            espaceService.getAll().stream()
                .map(Espace::getNom)
                .collect(Collectors.toList())
        );
    }

    @FXML
    private void handleBack() {
        // Fermer la fenêtre actuelle
        espacesList.getScene().getWindow().hide();
    }
}
