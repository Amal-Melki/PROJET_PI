package com.esprit.controllers;

import com.esprit.models.Evenement;
import com.esprit.services.EvenementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private TextField txtCategories;
    @FXML
    private TextField txtNbrPlaces;

    private EvenementService evenementService;

    public void initialize() {
        evenementService = new EvenementService();
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
                evenement.setCategories(txtCategories.getText());
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
        if (txtTitle.getText().isEmpty() || txtDescription.getText().isEmpty() ||
            dateDebut.getValue() == null || dateFin.getValue() == null ||
            txtLatitude.getText().isEmpty() || txtLongitude.getText().isEmpty() ||
            txtCategories.getText().isEmpty() || txtNbrPlaces.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs!");
            return false;
        }

        try {
            Integer.parseInt(txtNbrPlaces.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places doit être un nombre entier!");
            return false;
        }

        if (dateDebut.getValue().isAfter(dateFin.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début doit être antérieure à la date de fin!");
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