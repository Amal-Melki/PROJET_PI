package com.esprit.controllers;

import com.esprit.models.Evenement;
import com.esprit.services.EvenementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AjouterEvenementController {
    @FXML
    private TextField txtTitle;
    @FXML
    private TextArea txtDescription;
    @FXML
    private DatePicker dateDebut;
    @FXML
    private DatePicker dateFin;
    @FXML
    private TextField txtLatitude;
    @FXML
    private TextField txtLongitude;
    @FXML
    private ComboBox<String> cmbCategories;
    @FXML
    private TextField txtNbrPlaces;

    private EvenementService evenementService;
    private final ObservableList<String> categories = FXCollections.observableArrayList(
        "Célébration (anniversaire)",
        "Soirée/Gala",
        "Festival",
        "Lancement de produit"
    );

    public void initialize() {
        evenementService = new EvenementService();
        cmbCategories.setItems(categories);
        cmbCategories.setPromptText("Sélectionnez une catégorie");
    }

    @FXML
    private void handleAjouter() {
        if (validateInput()) {
            try {
                Evenement evenement = new Evenement();
                evenement.setTitle(txtTitle.getText());
                evenement.setDescription_ev(txtDescription.getText());
                evenement.setDate_debut(dateDebut.getValue());
                evenement.setDate_fin(dateFin.getValue());
                evenement.setLatitude(txtLatitude.getText());
                evenement.setLongitude(txtLongitude.getText());
                evenement.setCategories(cmbCategories.getValue());
                evenement.setNbr_places_dispo(Integer.parseInt(txtNbrPlaces.getText()));

                evenementService.ajouter(evenement);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès!");
                closeWindow();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'événement: " + e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        // Check for empty fields
        if (txtTitle.getText().isEmpty() || txtDescription.getText().isEmpty() ||
            dateDebut.getValue() == null || dateFin.getValue() == null ||
            txtLatitude.getText().isEmpty() || txtLongitude.getText().isEmpty() ||
            cmbCategories.getValue() == null || txtNbrPlaces.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs!");
            return false;
        }

        // Validate title length
        if (txtTitle.getText().length() < 3 || txtTitle.getText().length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre doit contenir entre 3 et 50 caractères!");
            return false;
        }

        // Validate description length
        if (txtDescription.getText().length() < 10 || txtDescription.getText().length() > 500) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description doit contenir entre 10 et 500 caractères!");
            return false;
        }

        // Validate dates
        LocalDate today = LocalDate.now();
        if (dateDebut.getValue().isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début ne peut pas être dans le passé!");
            return false;
        }

        if (dateDebut.getValue().isAfter(dateFin.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début doit être antérieure à la date de fin!");
            return false;
        }

        // Validate latitude and longitude format
        try {
            double latitude = Double.parseDouble(txtLatitude.getText());
            double longitude = Double.parseDouble(txtLongitude.getText());
            
            if (latitude < -90 || latitude > 90) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La latitude doit être comprise entre -90 et 90!");
                return false;
            }
            
            if (longitude < -180 || longitude > 180) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La longitude doit être comprise entre -180 et 180!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La latitude et la longitude doivent être des nombres valides!");
            return false;
        }

        // Validate number of places
        try {
            int nbrPlaces = Integer.parseInt(txtNbrPlaces.getText());
            if (nbrPlaces <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places doit être supérieur à 0!");
                return false;
            }
            if (nbrPlaces > 1000) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places ne peut pas dépasser 1000!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places doit être un nombre entier valide!");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }
} 