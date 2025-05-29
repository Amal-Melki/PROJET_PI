package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.services.EspaceService;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.*;

public class TreeMapController implements Initializable {

    @FXML private WebView webView;
    @FXML private ComboBox<String> comboType;
    @FXML private TextField txtMinCapacite;
    @FXML private TextField txtMaxCapacite;
    @FXML private TextField txtRechercheLocalisation;
    @FXML private Button btnRechercherLocalisation;
    @FXML private Button btnRetour;

    private WebEngine webEngine;
    private final EspaceService espaceService = new EspaceService();
    private List<Espace> allEspaces;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation du WebEngine
        webEngine = webView.getEngine();

        // Chargement de la page HTML avec OpenStreetMap
        webEngine.load(getClass().getClassLoader().getResource("views/osm_map.html").toExternalForm());

        // Configuration du bridge Java-JavaScript
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // Création du bridge
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", new JavaBridge());

                // Chargement initial des espaces
                loadEspaces();
            }
        });

        // Initialisation du ComboBox pour les types d'espaces
        initTypeFilter();
        
        // Configuration des validateurs pour les champs de capacité
        configureCapacityValidators();
        
        // Configuration de la touche Enter pour la recherche de localisation
        txtRechercheLocalisation.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                rechercherLocalisation();
            }
        });
    }
    
    /**
     * Initialise le filtre de type avec les valeurs uniques de la base de données
     */
    private void initTypeFilter() {
        // Récupérer tous les espaces et extraire les types uniques
        allEspaces = espaceService.getAll();
        
        // Utiliser un Set pour éviter les doublons
        Set<String> uniqueTypes = new HashSet<>();
        for (Espace espace : allEspaces) {
            if (espace.getType() != null && !espace.getType().isEmpty()) {
                uniqueTypes.add(espace.getType());
            }
        }
        
        // Trier les types par ordre alphabétique
        List<String> sortedTypes = new ArrayList<>(uniqueTypes);
        Collections.sort(sortedTypes);
        
        // Ajouter l'option "Tous les types" au début
        comboType.getItems().add("Tous les types");
        comboType.getItems().addAll(sortedTypes);
        comboType.getSelectionModel().selectFirst();
    }
    
    /**
     * Configure les validateurs pour les champs de capacité
     */
    private void configureCapacityValidators() {
        // Accepter uniquement les chiffres pour les champs de capacité
        txtMinCapacite.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtMinCapacite.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        txtMaxCapacite.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtMaxCapacite.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void loadEspaces() {
        allEspaces = espaceService.getAll();
        updateMapMarkers();
    }

    private void updateMapMarkers() {
        // Nettoyage des marqueurs existants
        webEngine.executeScript("clearMarkers();");

        // Ajout des nouveaux marqueurs
        for (Espace espace : allEspaces) {
            addMarkerToMap(espace);
        }
    }

    private void addMarkerToMap(Espace espace) {
        // Échapper les apostrophes dans les chaînes pour éviter les erreurs JavaScript
        String nomEscape = espace.getNom().replace("'", "\\'");
        String typeEscape = espace.getType().replace("'", "\\'");
        String localisationEscape = espace.getLocalisation().replace("'", "\\'");
        
        // Ajouter l'ID de l'espace et la disponibilité pour avoir des marqueurs de couleurs différentes
        String script = String.format(
                "addMarker(%f, %f, '%s', '%s', %d, '%s', %f, %d, %b);",
                getLatitudeFromLocation(espace.getLocalisation()),
                getLongitudeFromLocation(espace.getLocalisation()),
                nomEscape,
                typeEscape,
                espace.getCapacite(),
                localisationEscape,
                espace.getPrix(),
                espace.getId(),
                espace.isDisponibilite() // Passer la disponibilité pour changer la couleur du marqueur
        );
        webEngine.executeScript(script);
    }

    // Méthode pour géocoder une localisation (simplifiée)
    private double getLatitudeFromLocation(String location) {
        // Implémentation simplifiée - en pratique utiliser un service de géocodage
        if (location.contains("Tunis")) return 36.8065;
        if (location.contains("Sousse")) return 35.8254;
        if (location.contains("Hammamet")) return 36.4009;
        if (location.contains("Nabeul")) return 36.4561;
        return 36.8065; // Tunis par défaut
    }

    private double getLongitudeFromLocation(String location) {
        // Implémentation simplifiée - en pratique utiliser un service de géocodage
        if (location.contains("Tunis")) return 10.1815;
        if (location.contains("Sousse")) return 10.6360;
        if (location.contains("Hammamet")) return 10.6167;
        if (location.contains("Nabeul")) return 10.7376;
        return 10.1815; // Tunis par défaut
    }

    @FXML
    private void rechercherLocalisation() {
        String searchText = txtRechercheLocalisation.getText().trim();
        if (!searchText.isEmpty()) {
            // Centrer la carte sur la localisation recherchée
            String script = String.format(
                    "searchLocation('%s');",
                    searchText
            );
            webEngine.executeScript(script);
        }
    }

    @FXML
    private void appliquerFiltres() {
        // Filtrage des espaces selon les critères
        List<Espace> filtered = allEspaces.stream()
                .filter(e -> comboType.getValue() == null || comboType.getValue().isEmpty() || e.getType().equals(comboType.getValue()))
                .filter(e -> {
                    try {
                        int min = txtMinCapacite.getText().isEmpty() ? 0 : Integer.parseInt(txtMinCapacite.getText());
                        return e.getCapacite() >= min;
                    } catch (NumberFormatException ex) {
                        return true;
                    }
                })
                .filter(e -> {
                    try {
                        int max = txtMaxCapacite.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(txtMaxCapacite.getText());
                        return e.getCapacite() <= max;
                    } catch (NumberFormatException ex) {
                        return true;
                    }
                })
                .toList();

        // Mise à jour des marqueurs
        webEngine.executeScript("clearMarkers();");
        filtered.forEach(this::addMarkerToMap);
    }

    @FXML
    private void reinitialiserFiltres() {
        comboType.getSelectionModel().clearSelection();
        txtMinCapacite.clear();
        txtMaxCapacite.clear();
        txtRechercheLocalisation.clear();
        updateMapMarkers();
    }

    @FXML
    private void retourMenu() {
        // Code existant pour retourner au menu
    }

    // Classe bridge pour la communication Java-JavaScript
    public class JavaBridge {
        public void showEspaceDetails(int espaceId) {
            Espace espace = espaceService.getById(espaceId);
            if (espace != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Détails de l'espace");
                alert.setHeaderText(espace.getNom());
                alert.setContentText(
                        "Type: " + espace.getType() + "\n" +
                                "Capacité: " + espace.getCapacite() + "\n" +
                                "Localisation: " + espace.getLocalisation() + "\n" +
                                "Prix: " + espace.getPrix()
                );
                alert.showAndWait();
            }
        }
    }
}