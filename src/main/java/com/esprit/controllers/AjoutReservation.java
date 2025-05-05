package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
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

    @FXML
    private Button btnRetour;

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

        if (materiel == null || dateDebut == null || dateFin == null || quantiteText.isEmpty() || statut == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de début doit être antérieure à la date de fin.");
            return;
        }

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

        // Lire la quantité disponible en base
        ServiceMateriel serviceMateriel = new ServiceMateriel();
        int quantiteDisponible = serviceMateriel.getQuantiteById(materiel.getId());

        if (quantite > quantiteDisponible) {
            showAlert(Alert.AlertType.ERROR, "Quantité insuffisante", "Il ne reste que " + quantiteDisponible + " unité(s) disponible(s).");
            return;
        }

        // Vérification de doublon
        ServiceReservationMateriel serviceReservation = new ServiceReservationMateriel();
        for (ReservationMateriel r : serviceReservation.recuperer()) {
            if (r.getMaterielId() == materiel.getId()
                    && r.getDateDebut().toLocalDate().isEqual(dateDebut)
                    && r.getDateFin().toLocalDate().isEqual(dateFin)
                    && r.getQuantiteReservee() == quantite
                    && r.getStatut().equalsIgnoreCase(statut)) {
                showAlert(Alert.AlertType.WARNING, "Doublon", "Cette réservation existe déjà !");
                return;
            }
        }
        // Vérifier le statut du matériel
        if (!"DISPONIBLE".equalsIgnoreCase(materiel.getEtat())) {
            showAlert(Alert.AlertType.ERROR, "Matériel indisponible", "Ce matériel n'est pas disponible pour une réservation.");
            return;
        }

         // Vérifier si une autre réservation existe sur la même période
        for (ReservationMateriel r : serviceReservation.recuperer()) {
            if (r.getMaterielId() == materiel.getId()
                    && !(r.getDateFin().toLocalDate().isBefore(dateDebut) || r.getDateDebut().toLocalDate().isAfter(dateFin))) {
                showAlert(Alert.AlertType.ERROR, "Conflit", "Ce matériel est déjà réservé sur cette période.");
                return;
            }
        }


        // Création de la réservation
        ReservationMateriel reservation = new ReservationMateriel(
                0,
                materiel.getId(),
                dateDebut,
                dateFin,
                quantite,
                statut
        );

        serviceReservation.ajouter(reservation);
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

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Button getBtnReserver() {
        return btnReserver;
    }

    public void setBtnReserver(Button btnReserver) {
        this.btnReserver = btnReserver;
    }
}
