package com.esprit.controllers;

import com.esprit.modules.Espace;
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

        // TODO: Add reservation saving logic here

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