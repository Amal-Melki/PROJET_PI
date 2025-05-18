package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjoutReservationClient implements Initializable {

    @FXML
    private Label lblNomMateriel;

    @FXML
    private DatePicker dpDebut;

    @FXML
    private DatePicker dpFin;

    @FXML
    private TextField tfQuantite;

    @FXML
    private TextField tfMontantTotal;

    @FXML
    private Button btnReserver;

    @FXML
    private Button btnRetour;

    private final int clientId = 1; // ID fictif du client connecté

    private Materiels materielSelectionne;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfMontantTotal.setEditable(false);

        tfQuantite.textProperty().addListener((obs, oldVal, newVal) -> calculerMontantTotal());
    }

    private void calculerMontantTotal() {
        if (materielSelectionne == null || tfQuantite.getText().trim().isEmpty()) {
            tfMontantTotal.clear();
            return;
        }

        try {
            int quantite = Integer.parseInt(tfQuantite.getText().trim());
            double total = quantite * materielSelectionne.getPrix();
            tfMontantTotal.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            tfMontantTotal.clear();
        }
    }
    @FXML
    void reserverMateriel() {
        if (materielSelectionne == null || dpDebut.getValue() == null || dpFin.getValue() == null || tfQuantite.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        // ✅ Vérification que la date de début n'est pas dans le passé
        LocalDate today = LocalDate.now();
        if (dpDebut.getValue().isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être antérieure à aujourd’hui.");
            return;
        }

        if (!dpDebut.getValue().isBefore(dpFin.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "La date de début doit précéder la date de fin.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(tfQuantite.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide.");
            return;
        }

        if (quantite <= 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être positive.");
            return;
        }

        if (!"DISPONIBLE".equalsIgnoreCase(materielSelectionne.getEtat())) {
            showAlert(Alert.AlertType.WARNING, "Indisponible", "Ce matériel n'est pas disponible.");
            return;
        }

        ServiceMateriel sm = new ServiceMateriel();
        int quantiteStock = sm.getQuantiteById(materielSelectionne.getId());
        if (quantite > quantiteStock) {
            showAlert(Alert.AlertType.WARNING, "Stock insuffisant", "Il ne reste que " + quantiteStock + " unités disponibles.");
            return;
        }

        double montant = quantite * materielSelectionne.getPrix();

        ReservationMateriel reservation = new ReservationMateriel();
        reservation.setMaterielId(materielSelectionne.getId());
        reservation.setDateDebut(Date.valueOf(dpDebut.getValue()));
        reservation.setDateFin(Date.valueOf(dpFin.getValue()));
        reservation.setQuantiteReservee(quantite);
        reservation.setStatut("EN_ATTENTE");
        reservation.setMontantTotal(montant);
        reservation.setIdClient(clientId);

        new ServiceReservationMateriel().ajouter(reservation);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation enregistrée !");
        resetForm();
    }


    private void resetForm() {
        tfQuantite.clear();
        tfMontantTotal.clear();
        dpDebut.setValue(null);
        dpFin.setValue(null);
    }

    @FXML
    private void retourAccueil() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Accueil.fxml"));
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Appelée par ListeMaterielsClient pour injecter l'objet
    public void setMaterielSelectionne(Materiels m) {
        this.materielSelectionne = m;
        if (lblNomMateriel != null) {
            lblNomMateriel.setText(m.getNom());
            calculerMontantTotal(); // mettre à jour le montant
        }
    }
}
