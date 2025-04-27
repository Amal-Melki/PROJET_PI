package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class DetailsReservationController {

    public TableColumn colDateFin;
    @FXML
    private Label reservationIdLabel;
    @FXML
    private Label espaceLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label dateDebutLabel;
    @FXML
    private Label dateFinLabel;
    @FXML
    private Button fermerButton;

    // Méthode pour afficher les détails d'une réservation
    public void afficherDetails(String reservationId, String espace, String utilisateur, String dateDebut, String dateFin) {
        reservationIdLabel.setText(reservationId);
        espaceLabel.setText(espace);
        userLabel.setText(utilisateur);
        dateDebutLabel.setText(dateDebut);
        dateFinLabel.setText(dateFin);
    }

    // Méthode pour fermer la fenêtre
    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }

    public void supprimerReservation(ActionEvent actionEvent) {
    }

    public void retourMenu(ActionEvent actionEvent) {
    }

    public void detailsReservation(ActionEvent actionEvent) {
    }
}