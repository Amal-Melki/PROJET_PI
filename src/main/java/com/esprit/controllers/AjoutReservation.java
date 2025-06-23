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
import java.time.LocalDate; // Import correct
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


public class AjoutReservation implements Initializable {

    @FXML private ComboBox<Materiels> cbMateriel;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;
    @FXML private TextField tfQuantite;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Button btnReserver;
    @FXML private Button btnRetour;
    @FXML private TextField tfMontantTotal;
    @FXML
    private ImageView logoImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServiceMateriel sm = new ServiceMateriel();
        List<Materiels> listeMateriels = sm.rechercher();
        cbMateriel.setItems(FXCollections.observableArrayList(listeMateriels));
        cbStatut.setItems(FXCollections.observableArrayList("EN_ATTENTE", "VALIDEE", "ANNULEE"));
        cbStatut.setValue("EN_ATTENTE");
        tfQuantite.textProperty().addListener((observable, oldValue, newValue) -> calculerMontantTotal());
        cbMateriel.valueProperty().addListener((obs, oldMat, newMat) -> calculerMontantTotal());
        try {
            Image img = new Image(getClass().getResource("/logo.jpg").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
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

        // 🔒 Vérification que la date de début n'est pas dans le passé
        LocalDate today = LocalDate.now();
        if (dateDebut.isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être dans le passé.");
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

        ServiceMateriel serviceMateriel = new ServiceMateriel();
        int quantiteDisponible = serviceMateriel.getQuantiteById(materiel.getId());

        if (quantite > quantiteDisponible) {
            showAlert(Alert.AlertType.ERROR, "Quantité insuffisante", "Il ne reste que " + quantiteDisponible + " unité(s) disponible(s).");
            return;
        }

        if (!"DISPONIBLE".equalsIgnoreCase(materiel.getEtat())) {
            showAlert(Alert.AlertType.ERROR, "Matériel indisponible", "Ce matériel n'est pas disponible pour une réservation.");
            return;
        }

        ServiceReservationMateriel serviceReservation = new ServiceReservationMateriel();
        for (ReservationMateriel r : serviceReservation.rechercher()) {
            // Cette ligne est maintenant correcte car r.getDateFin() et r.getDateDebut() seront des LocalDate
            if (r.getMaterielId() == materiel.getId()
                    && !(r.getDateFin().isBefore(dateDebut) || r.getDateDebut().isAfter(dateFin))) {
                showAlert(Alert.AlertType.ERROR, "Conflit", "Ce matériel est déjà réservé sur cette période.");
                return;
            }
        }

        double montantTotal = materiel.getPrix() * quantite;

        ReservationMateriel reservation = new ReservationMateriel(
                0,
                materiel.getId(),
                dateDebut,
                dateFin,
                quantite,
                statut
        );
        reservation.setMontantTotal(montantTotal);
        // Si vous utilisez 'ajouter' et que idClient est obligatoire, assurez-vous de le définir ici
        // reservation.setIdClient(votreIdDuClientConnecte);

        serviceReservation.ajouteradmin(reservation); // Ou serviceReservation.ajouter(reservation); si idClient est géré
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
        tfMontantTotal.clear();
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

    private void calculerMontantTotal() {
        Materiels materiel = cbMateriel.getValue();
        String quantiteText = tfQuantite.getText().trim();

        if (materiel != null && !quantiteText.isEmpty() && quantiteText.matches("\\d+")) {
            try {
                int quantite = Integer.parseInt(quantiteText);
                double montant = quantite * materiel.getPrix();
                tfMontantTotal.setText(String.format("%.2f", montant));
            } catch (NumberFormatException e) {
                tfMontantTotal.clear();
            }
        } else {
            tfMontantTotal.clear();
        }
    }

    public Button getBtnReserver() {
        return btnReserver;
    }

    public void setBtnReserver(Button btnReserver) {
        this.btnReserver = btnReserver;
    }
}