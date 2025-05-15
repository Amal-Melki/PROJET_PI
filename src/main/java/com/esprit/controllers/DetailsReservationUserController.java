package com.esprit.controllers;

import com.esprit.modules.Espace;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class DetailsReservationUserController {

    // FXML variables
    @FXML
    private Button btnRetour;

    @FXML
    private Button btnAnnulerReservation;

    @FXML
    private Label nomEspaceLabel;

    @FXML
    private Label typeEspaceLabel;

    @FXML
    private Label capaciteEspaceLabel;

    @FXML
    private Label localisationEspaceLabel;

    @FXML
    private Label prixEspaceLabel;

    @FXML
    private Label reservationIdLabel;

    @FXML
    private Label dateDebutLabel;

    @FXML
    private Label dateFinLabel;

    @FXML
    private Label statutLabel;

    // Method to handle the return action
    @FXML
    private void retourMesReservations(javafx.event.ActionEvent event) {
        chargerInterface("/MesReservationsClient.fxml", event);
    }

    private void chargerInterface(String fxmlPath, javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle reservation cancellation
    @FXML
    private void annulerReservation() {
        // Implement reservation cancellation logic
        System.out.println("Réservation annulée");
    }

    // Method to initialize and set the details for the reservation
    public void initialize(String nom, String type, int capacite, String localisation, double prix,
                           String reservationId, String dateDebut, String dateFin, String statut) {
        nomEspaceLabel.setText(nom);
        typeEspaceLabel.setText(type);
        capaciteEspaceLabel.setText(String.valueOf(capacite));
        localisationEspaceLabel.setText(localisation);
        prixEspaceLabel.setText(String.valueOf(prix));
        reservationIdLabel.setText(reservationId);
        dateDebutLabel.setText(dateDebut);
        dateFinLabel.setText(dateFin);
        statutLabel.setText(statut);
    }

    public void initData(Espace currentEspace, LocalDate dateDebut, LocalDate dateFin) {
        if (currentEspace != null && dateDebut != null && dateFin != null) {
            initialize(
                currentEspace.getNom(),
                currentEspace.getType(),
                currentEspace.getCapacite(),
                currentEspace.getLocalisation(),
                currentEspace.getPrix(),
                "N/A", // reservationId not available here
                dateDebut.toString(),
                dateFin.toString(),
                "Confirmée" // Assuming status is confirmed
            );
        }
    }
}
