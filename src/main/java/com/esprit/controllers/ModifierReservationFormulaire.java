package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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

    private ReservationMateriel reservationToModify;
    private Materiels materielAssocie;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbStatut.setItems(FXCollections.observableArrayList("EN_ATTENTE", "VALIDEE", "ANNULEE"));
    }

    public void setReservation(ReservationMateriel r) {
        this.reservationToModify = r;

        // Récupérer le matériel correspondant pour la quantité
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

        // Vérifications
        if (materielAssocie == null || dateDebut == null || dateFin == null || quantiteText.isEmpty() || statut == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de début doit être avant la date de fin.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être un nombre entier.");
            return;
        }

        if (quantite <= 0 || quantite > materielAssocie.getQuantite()) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Quantité non disponible (stock max : " + materielAssocie.getQuantite() + ")");
            return;
        }

        // Mise à jour
        reservationToModify.setMaterielId(materielAssocie.getId()); // inchangé
        reservationToModify.setDateDebut(java.sql.Date.valueOf(dateDebut));
        reservationToModify.setDateFin(java.sql.Date.valueOf(dateFin));
        reservationToModify.setQuantiteReservee(quantite);
        reservationToModify.setStatut(statut);

        ServiceReservationMateriel service = new ServiceReservationMateriel();
        service.modifier(reservationToModify);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation mise à jour avec succès !");
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
