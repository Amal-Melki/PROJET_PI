package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.ReservationEspaceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.io.IOException;

public class AjouterReservationClient {

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
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private Button btnConfirmer;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnRetour;

    private Espace espace;
    private ReservationEspaceService reservationService;
    private String currentUser = "client_exemple"; // Dans un cas réel, ceci viendrait d'un service d'authentification

    public AjouterReservationClient(Espace espace) {
        this.espace = espace;
        this.reservationService = new ReservationEspaceService();
    }

    public AjouterReservationClient() {
        this.reservationService = new ReservationEspaceService();
    }

    @FXML
    public void initialize() {
        if (espace != null) {
            nomEspaceLabel.setText(espace.getNom());
            typeEspaceLabel.setText(espace.getType());
            capaciteEspaceLabel.setText(String.valueOf(espace.getCapacite()));
            localisationEspaceLabel.setText(espace.getLocalisation());
            prixEspaceLabel.setText(String.valueOf(espace.getPrix()) + " DT");
        }

        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateFinPicker.getValue() != null &&
                    newVal.isAfter(dateFinPicker.getValue())) {
                dateFinPicker.setValue(newVal.plusDays(1));
            }
        });
    }

    @FXML
    public void confirmerReservation(ActionEvent event) {
        if (espace == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun espace sélectionné.");
            return;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut == null || dateFin == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner des dates valides.");
            return;
        }

        if (dateDebut.isAfter(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début doit être avant la date de fin.");
            return;
        }

        try {
            reservationService.creerReservation(currentUser, espace.getId(), dateDebut, dateFin);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation confirmée avec succès!");
            retourConsulterEspaces(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la réservation: " + e.getMessage());
        }
    }

    @FXML
    public void annulerReservation(ActionEvent event) {
        retourConsulterEspaces(event);
    }

    @FXML
    public void retourConsulterEspaces(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/views/ConsulterEspacesClient.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}