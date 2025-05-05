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

public class ModifierReservationFormulaire implements Initializable {

    @FXML
    private Label lblNomMateriel;

    @FXML
    private DatePicker dpDebut;

    @FXML
    private DatePicker dpFin;

    @FXML
    private TextField tfQuantite;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private Button btnEnregistrer;

    @FXML
    private Button btnRetour;

    private ReservationMateriel reservationToModify;
    private Materiels materielAssocie;

    private int ancienneQuantiteReservee; // 🔥 Pour recalculer le stock correctement

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbStatut.setItems(FXCollections.observableArrayList("EN_ATTENTE", "VALIDEE", "ANNULEE"));
    }

    public void setReservation(ReservationMateriel r) {
        this.reservationToModify = r;
        this.ancienneQuantiteReservee = r.getQuantiteReservee();

        // 🔥 Récupérer le matériel associé
        ServiceMateriel sm = new ServiceMateriel();
        for (Materiels m : sm.recuperer()) {
            if (m.getId() == r.getMaterielId()) {
                materielAssocie = m;
                lblNomMateriel.setText(m.getNom());
                break;
            }
        }

        dpDebut.setValue(r.getDateDebut().toLocalDate());
        dpFin.setValue(r.getDateFin().toLocalDate());
        tfQuantite.setText(String.valueOf(r.getQuantiteReservee()));
        cbStatut.setValue(r.getStatut());
    }

    @FXML
    void modifierReservation() {
        LocalDate dateDebut = dpDebut.getValue();
        LocalDate dateFin = dpFin.getValue();
        String quantiteText = tfQuantite.getText().trim();
        String statut = cbStatut.getValue();

        if (materielAssocie == null || dateDebut == null || dateFin == null || quantiteText.isEmpty() || statut == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de début doit être avant la date de fin.");
            return;
        }

        int nouvelleQuantite;
        try {
            nouvelleQuantite = Integer.parseInt(quantiteText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être un nombre entier positif.");
            return;
        }

        if (nouvelleQuantite <= 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être positive.");
            return;
        }

        ServiceMateriel sm = new ServiceMateriel();
        int quantiteStockActuelle = sm.getQuantiteById(materielAssocie.getId());

        // 🔥 Important : on remet d'abord la quantité ancienne avant de recalculer
        int stockTemporaire = quantiteStockActuelle + ancienneQuantiteReservee;

        if (stockTemporaire - nouvelleQuantite < 0) {
            showAlert(Alert.AlertType.ERROR, "Stock insuffisant", "Pas assez de stock pour cette modification.");
            return;
        }

        // Mise à jour de la réservation
        reservationToModify.setDateDebut(java.sql.Date.valueOf(dateDebut));
        reservationToModify.setDateFin(java.sql.Date.valueOf(dateFin));
        reservationToModify.setQuantiteReservee(nouvelleQuantite);
        reservationToModify.setStatut(statut);

        ServiceReservationMateriel srm = new ServiceReservationMateriel();
        srm.modifier(reservationToModify);

        // 🔥 Mise à jour du stock matériel
        int stockFinal = stockTemporaire - nouvelleQuantite;
        sm.mettreAJourQuantite(materielAssocie.getId(), stockFinal);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation modifiée et stock mis à jour avec succès !");


    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReservation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Réservations");
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
