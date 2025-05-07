package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsulterEspacesClient {

    @FXML
    private TableView<Espace> tableEspaces;
    @FXML
    private TableColumn<Espace, String> colNom;
    @FXML
    private TableColumn<Espace, String> colType;
    @FXML
    private TableColumn<Espace, Integer> colCapacite;
    @FXML
    private TableColumn<Espace, String> colLocalisation;
    @FXML
    private TableColumn<Espace, Double> colPrix;
    @FXML
    private Button btnReserver;
    @FXML
    private Button btnRetour;
    @FXML
    private Button btnMesReservations;
    @FXML
    private TextField rechercheField;
    @FXML
    private ComboBox<String> filterTypeCombo;

    private EspaceService espaceService;
    private ObservableList<Espace> espacesList;
    private ObservableList<Espace> filteredEspacesList;

    @FXML
    public void initialize() {
        espaceService = new EspaceService();

        // Configurer les colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Initialiser la liste des types d'espaces pour le filtre
        List<Espace> allEspaces = espaceService.getAll();
        List<String> types = allEspaces.stream()
                .map(Espace::getType)
                .distinct()
                .collect(Collectors.toList());

        filterTypeCombo.getItems().add("Tous les types");
        filterTypeCombo.getItems().addAll(types);
        filterTypeCombo.setValue("Tous les types");

        // Ajouter les écouteurs d'événements
        filterTypeCombo.setOnAction(event -> filtrerEspaces());
        rechercheField.textProperty().addListener((observable, oldValue, newValue) -> filtrerEspaces());

        // Charger tous les espaces disponibles
        chargerEspaces();

        // Configurer les boutons
        btnReserver.setOnAction(this::reserverEspace);
        btnMesReservations.setOnAction(this::voirMesReservations);
        btnRetour.setOnAction(this::retourMenu);
    }

    private void chargerEspaces() {
        // Ne charger que les espaces disponibles
        List<Espace> espacesDisponibles = espaceService.getAll().stream()
                .filter(Espace::isDisponibilite)
                .collect(Collectors.toList());

        espacesList = FXCollections.observableArrayList(espacesDisponibles);
        filteredEspacesList = FXCollections.observableArrayList(espacesDisponibles);
        tableEspaces.setItems(filteredEspacesList);
    }

    private void filtrerEspaces() {
        String recherche = rechercheField.getText().toLowerCase();
        String typeFiltre = filterTypeCombo.getValue();

        List<Espace> espacesFiltres = espacesList.stream()
                .filter(espace -> {
                    boolean matchRecherche = recherche.isEmpty() ||
                            espace.getNom().toLowerCase().contains(recherche) ||
                            espace.getLocalisation().toLowerCase().contains(recherche);

                    boolean matchType = "Tous les types".equals(typeFiltre) ||
                            espace.getType().equals(typeFiltre);

                    return matchRecherche && matchType;
                })
                .collect(Collectors.toList());

        filteredEspacesList.clear();
        filteredEspacesList.addAll(espacesFiltres);
    }

    @FXML
    public void reserverEspace(ActionEvent event) {
        Espace selectedEspace = tableEspaces.getSelectionModel().getSelectedItem();
        if (selectedEspace != null) {
            try {
                // Charger le fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/AjouterReservationClient.fxml"));

                // Créer une instance du contrôleur et lui passer l'espace sélectionné
                AjouterReservationClient controller = new AjouterReservationClient(selectedEspace);
                loader.setController(controller);

                Parent root = loader.load();

                // Obtenir la scène actuelle et la modifier
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                        "Impossible d'accéder à la page de réservation: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise",
                    "Veuillez sélectionner un espace à réserver.");
        }
    }

    @FXML
    public void voirMesReservations(ActionEvent event) {
        try {
            // Charger le fichier FXML
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/views/MesReservationsClient.fxml"));

            // Obtenir la scène actuelle et la modifier
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible d'accéder à mes réservations: " + e.getMessage());
        }
    }

    @FXML
    public void retourMenu(ActionEvent event) {
        try {
            // Charger le fichier FXML
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/views/MenuEspace.fxml"));

            // Obtenir la scène actuelle et la modifier
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de retourner au menu: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void rafraichirListe(ActionEvent actionEvent) {
    }
}