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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierReservationClient implements Initializable {

    @FXML private Label lblNomMateriel;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;
    @FXML private TextField tfQuantite;

    @FXML private Button btnEnregistrer;
    @FXML private Button btnRetour;
    @FXML private TextField tfMontantTotal;

    private ReservationMateriel reservationToModify;
    private Materiels materielAssocie;
    private int ancienneQuantiteReservee;
    @FXML
    private ImageView logoImage;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
        tfQuantite.textProperty().addListener((obs, oldVal, newVal) -> {
            calculerMontantTotal();
        });
    }

    public void setReservation(ReservationMateriel r) {
        this.reservationToModify = r;
        this.ancienneQuantiteReservee = r.getQuantiteReservee();

        ServiceMateriel sm = new ServiceMateriel();
        for (Materiels m : sm.rechercher()) {
            if (m.getId() == r.getMaterielId()) {
                materielAssocie = m;
                lblNomMateriel.setText(m.getNom());
                break;
            }
        }

        dpDebut.setValue(r.getDateDebut().toLocalDate());
        dpFin.setValue(r.getDateFin().toLocalDate());
        tfQuantite.setText(String.valueOf(r.getQuantiteReservee()));


        calculerMontantTotal();
    }

    private void calculerMontantTotal() {
        try {
            int qte = Integer.parseInt(tfQuantite.getText().trim());
            if (materielAssocie != null) {
                double montant = qte * materielAssocie.getPrix();
                tfMontantTotal.setText(String.format("%.2f", montant));
            }
        } catch (NumberFormatException e) {
            tfMontantTotal.setText("");
        }
    }

    @FXML
    void modifierReservation() {
        LocalDate dateDebut = dpDebut.getValue();
        LocalDate dateFin = dpFin.getValue();
        String quantiteText = tfQuantite.getText().trim();


        if (materielAssocie == null || dateDebut == null || dateFin == null || quantiteText.isEmpty() ) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // ✅ Vérification de la date de début : pas dans le passé
        LocalDate today = LocalDate.now();
        if (dateDebut.isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être dans le passé.");
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
        int stockTemporaire = quantiteStockActuelle + ancienneQuantiteReservee;

        if (stockTemporaire - nouvelleQuantite < 0) {
            showAlert(Alert.AlertType.ERROR, "Stock insuffisant", "Pas assez de stock pour cette modification.");
            return;
        }

        // ✅ Mise à jour des champs
        reservationToModify.setDateDebut(java.sql.Date.valueOf(dateDebut));
        reservationToModify.setDateFin(java.sql.Date.valueOf(dateFin));
        reservationToModify.setQuantiteReservee(nouvelleQuantite);

        reservationToModify.setMontantTotal(nouvelleQuantite * materielAssocie.getPrix());

        new ServiceReservationMateriel().modifier(reservationToModify);
        sm.mettreAJourQuantite(materielAssocie.getId(), stockTemporaire - nouvelleQuantite);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeReservationsClient.fxml"));
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
