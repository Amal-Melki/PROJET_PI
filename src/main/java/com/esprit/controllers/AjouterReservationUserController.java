package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EmailService;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class AjouterReservationUserController {

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnConfirmer;

    @FXML
    private Button btnAnnuler;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

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
    private Text titreReservation;

    private Espace currentEspace;

    public void initData(Espace espaceSelectionne) {
        if (espaceSelectionne != null) {
            currentEspace = espaceSelectionne;
            nomEspaceLabel.setText(espaceSelectionne.getNom());
            typeEspaceLabel.setText(espaceSelectionne.getType());
            capaciteEspaceLabel.setText(String.valueOf(espaceSelectionne.getCapacite()));
            localisationEspaceLabel.setText(espaceSelectionne.getLocalisation());
            prixEspaceLabel.setText(String.valueOf(espaceSelectionne.getPrix()));
        }
    }

    @FXML
    public void confirmerReservation(ActionEvent event) {
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une période valide pour la réservation.");
            alert.showAndWait();
            return;
        }

        if (currentEspace == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Aucun espace sélectionné pour la réservation.");
            alert.showAndWait();
            return;
        }

        // Add reservation saving logic here
        String currentUserEmail = "amalmelki346@gmail.com"; // Temporary email until user info is available

        // Save reservation
        ReservationEspaceService reservationService = new ReservationEspaceService();
        reservationService.creerReservation(currentUserEmail, currentEspace.getId(), dateDebutPicker.getValue(), dateFinPicker.getValue());

        // Fetch espace details
        EspaceService espaceService = new EspaceService();
        var espaceDetails = espaceService.getEspaceById(currentEspace.getId());

        // Construct email body with reservation and espace details
        String emailBody = "Bonjour,\n\nVotre réservation a été confirmée avec les détails suivants :\n" +
                "Espace : " + (espaceDetails != null ? espaceDetails.getNom() : "N/A") + "\n" +
                "Type : " + (espaceDetails != null ? espaceDetails.getType() : "N/A") + "\n" +
                "Capacité : " + (espaceDetails != null ? espaceDetails.getCapacite() : "N/A") + "\n" +
                "Localisation : " + (espaceDetails != null ? espaceDetails.getLocalisation() : "N/A") + "\n" +
                "Prix : " + (espaceDetails != null ? espaceDetails.getPrix() : "N/A") + "\n" +
                "Date début : " + dateDebutPicker.getValue() + "\n" +
                "Date fin : " + dateFinPicker.getValue() + "\n\nMerci.";

        // Send email
        EmailService emailService = new EmailService();
        try {
            emailService.sendEmail(currentUserEmail, "Confirmation de réservation", emailBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show confirmation alert
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Réservation confirmée");
        alert.setHeaderText(null);
        alert.setContentText("Votre réservation a été confirmée et un email de confirmation a été envoyé.");
        alert.showAndWait();

        // After successful reservation, navigate to DetailsReservationUser.fxml to show reservation details
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsReservationUser.fxml"));
            Parent root = loader.load();

            DetailsReservationUserController controller = loader.getController();
            // Pass reservation details to the details controller (implement as needed)
            controller.initData(currentEspace, dateDebutPicker.getValue(), dateFinPicker.getValue());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void creerReservation(String currentUserEmail, int id, LocalDate value, LocalDate value1) {
    }

    @FXML
    public void annulerReservation(ActionEvent event) {
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Annulation");
        alert.setHeaderText(null);
        alert.setContentText("Réservation annulée.");
        alert.showAndWait();
    }

    @FXML
    public void retourConsulterEspaces(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConsulterEspacesUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}