package com.esprit.core.controllers.controllers;

import com.esprit.controllers.ModifierEvenementController;
import com.esprit.models.Evenement;
import com.esprit.services.EvenementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class EvenementsController {
    @FXML
    private TableView<Evenement> tableEvenements;
    
    @FXML
    private TableColumn<Evenement, String> colTitle;
    
    @FXML
    private TableColumn<Evenement, String> colDescription;
    
    @FXML
    private TableColumn<Evenement, LocalDate> colDateDebut;
    
    @FXML
    private TableColumn<Evenement, LocalDate> colDateFin;
    
    @FXML
    private TableColumn<Evenement, String> colLatitude;
    
    @FXML
    private TableColumn<Evenement, String> colLongitude;
    
    @FXML
    private TableColumn<Evenement, String> colCategories;
    
    @FXML
    private TableColumn<Evenement, Integer> colNbrPlaces;
    
    @FXML
    private TableColumn<Evenement, Void> colModifier;
    
    @FXML
    private TableColumn<Evenement, Void> colSupprimer;
    
    @FXML
    private TextField searchField;
    
    private EvenementService evenementService;
    private ObservableList<Evenement> evenementsList;
    private FilteredList<Evenement> filteredData;

    @FXML
    public void initialize() {
        evenementService = new EvenementService();
        evenementsList = FXCollections.observableArrayList();
        
        // Configure table columns
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description_ev"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
        colLatitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        colLongitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        colCategories.setCellValueFactory(new PropertyValueFactory<>("categories"));
        colNbrPlaces.setCellValueFactory(new PropertyValueFactory<>("nbr_places_dispo"));
        
        // Configure modifier column
        colModifier.setCellFactory(col -> new TableCell<>() {
            private final Button btnModifier = new Button("✏️");
            {
                btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnModifier.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    handleModifier(evenement);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnModifier);
            }
        });
        
        // Configure supprimer column
        colSupprimer.setCellFactory(col -> new TableCell<>() {
            private final Button btnSupprimer = new Button("🗑️");
            {
                btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                btnSupprimer.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    handleSupprimer(evenement);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSupprimer);
            }
        });
        
        // Set up search functionality
        setupSearch();
        
        // Load data
        loadEvenements();
    }
    
    private void setupSearch() {
        filteredData = new FilteredList<>(evenementsList, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(evenement -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (evenement.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (evenement.getDescription_ev().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (evenement.getCategories().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });
        
        SortedList<Evenement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableEvenements.comparatorProperty());
        tableEvenements.setItems(sortedData);
    }
    
    private void loadEvenements() {
        evenementsList.clear();
        evenementsList.addAll(evenementService.rechercher());
    }
    
    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh table after adding
            loadEvenements();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire d'ajout");
        }
    }
    
    private void handleModifier(Evenement evenement) {
        if (!validateEvenement(evenement)) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenement.fxml"));
            Parent root = loader.load();
            
            ModifierEvenementController controller = loader.getController();
            controller.setEvenement(evenement);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh table after modification
            loadEvenements();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de modification");
        }
    }
    
    private boolean validateEvenement(Evenement evenement) {
        if (evenement == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné");
            return false;
        }

        // Validate title
        if (evenement.getTitle() == null || evenement.getTitle().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre ne peut pas être vide");
            return false;
        }

        if (evenement.getTitle().length() < 3 || evenement.getTitle().length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre doit contenir entre 3 et 50 caractères");
            return false;
        }

        // Validate description
        if (evenement.getDescription_ev() == null || evenement.getDescription_ev().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description ne peut pas être vide");
            return false;
        }

        if (evenement.getDescription_ev().length() < 10 || evenement.getDescription_ev().length() > 500) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description doit contenir entre 10 et 500 caractères");
            return false;
        }

        // Validate dates
        if (evenement.getDate_debut() == null || evenement.getDate_fin() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les dates ne peuvent pas être vides");
            return false;
        }

        LocalDate today = LocalDate.now();
        if (evenement.getDate_debut().isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début ne peut pas être dans le passé");
            return false;
        }

        if (evenement.getDate_debut().isAfter(evenement.getDate_fin())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début doit être antérieure à la date de fin");
            return false;
        }

        // Validate coordinates
        try {
            double latitude = Double.parseDouble(evenement.getLatitude());
            double longitude = Double.parseDouble(evenement.getLongitude());
            
            if (latitude < -90 || latitude > 90) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La latitude doit être comprise entre -90 et 90");
                return false;
            }
            
            if (longitude < -180 || longitude > 180) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La longitude doit être comprise entre -180 et 180");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les coordonnées doivent être des nombres valides");
            return false;
        }

        // Validate number of places
        if (evenement.getNbr_places_dispo() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places doit être supérieur à 0");
            return false;
        }

        if (evenement.getNbr_places_dispo() > 1000) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places ne peut pas dépasser 1000");
            return false;
        }

        return true;
    }
    
    private void    handleSupprimer(Evenement evenement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet événement ?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            evenementService.supprimer(evenement);
            loadEvenements();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement supprimé avec succès");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 