package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
import com.esprit.services.ServiceReservationMateriel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AjoutReservation implements Initializable {

    @FXML
    private ComboBox<Materiels> cbMateriel;

    @FXML
    private DatePicker dpDebut;

    @FXML
    private DatePicker dpFin;

    @FXML
    private TextField tfQuantite;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private Button btnReserver;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger les matériels
        ServiceMateriel sm = new ServiceMateriel();
        List<Materiels> listeMateriels = sm.recuperer();
        cbMateriel.setItems(FXCollections.observableArrayList(listeMateriels));

        // Charger les statuts possibles
        cbStatut.setItems(FXCollections.observableArrayList("EN_ATTENTE", "VALIDEE", "ANNULEE"));
        cbStatut.setValue("EN_ATTENTE"); // Valeur par défaut
    }

    @FXML
    void reserverMateriel() {
        Materiels materiel = cbMateriel.getValue();
        LocalDate dateDebut = dpDebut.getValue();
        LocalDate dateFin = dpFin.getValue();
        String quantiteText = tfQuantite.getText().trim();
        String statut = cbStatut.getValue();

        // Vérification de champs vides
        if (materiel == null || dateDebut == null || dateFin == null || quantiteText.isEmpty() || statut == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification des dates
        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de début doit être antérieure à la date de fin.");
            return;
        }

        // Vérification de la quantité
        int quantite;
        try {
            quantite = Integer.parseInt(quantiteText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être un nombre entier.");
            return;
        }

        if (quantite <= 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être positive.");
            return;
        }

        if (quantite > materiel.getQuantite()) {
            showAlert(Alert.AlertType.ERROR, "Quantité insuffisante", "La quantité demandée dépasse celle disponible (" + materiel.getQuantite() + ").");
            return;
        }

        // Création et enregistrement de la réservation
        ReservationMateriel reservation = new ReservationMateriel(
                0,
                materiel.getId(),
                dateDebut,
                dateFin,
                quantite,
                statut
        );

        ServiceReservationMateriel sr = new ServiceReservationMateriel();
        sr.ajouter(reservation);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation enregistrée avec succès !");
        resetForm();
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void resetForm() {
        cbMateriel.getSelectionModel().clearSelection();
        dpDebut.setValue(null);
        dpFin.setValue(null);
        tfQuantite.clear();
        cbStatut.setValue("EN_ATTENTE");
    }
}
