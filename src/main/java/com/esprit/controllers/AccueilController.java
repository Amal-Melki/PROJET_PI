package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {

    @FXML private Button btnVoirEvenements;
    @FXML private Button btnAjoutMateriel;
    @FXML private Button btnListeMateriels;
    @FXML private Button btnAjoutFournisseur;
    @FXML private Button btnListeFournisseurs;
    @FXML private Button btnAjoutReservation;
    @FXML private Button btnListeReservations;
    @FXML private Button btnAjoutReservationClient;
    @FXML private Button btnListeMaterielsClient;
    @FXML private Button btnListeReservationsClient;
    @FXML private ImageView logoImage;

    @FXML
    public void initialize() {
        if (btnAjoutMateriel != null) btnAjoutMateriel.setOnAction(this::ouvrirAjoutMateriel);
        if (btnListeMateriels != null) btnListeMateriels.setOnAction(this::ouvrirListeMateriels);
        if (btnAjoutFournisseur != null) btnAjoutFournisseur.setOnAction(this::ouvrirAjoutFournisseur);
        if (btnListeFournisseurs != null) btnListeFournisseurs.setOnAction(this::ouvrirListeFournisseurs);
        if (btnAjoutReservation != null) btnAjoutReservation.setOnAction(this::ouvrirAjoutReservation);
        if (btnListeReservations != null) btnListeReservations.setOnAction(this::ouvrirListeReservations);
        if (btnAjoutReservationClient != null) btnAjoutReservationClient.setOnAction(this::ouvrirAjoutReservationsClient);
        if (btnListeMaterielsClient != null) btnListeMaterielsClient.setOnAction(this::ouvrirListeMaterielsClient);
        if (btnListeReservationsClient != null) btnListeReservationsClient.setOnAction(this::ouvrirListeReservationsClient);
        if (btnVoirEvenements != null) btnVoirEvenements.setOnAction(e -> handleVoirEvenements());

        try {
            if (logoImage != null) {
                Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
                logoImage.setImage(img);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }

    @FXML
    private void handleVoirEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvenementsFront.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnVoirEvenements.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ouvrirAjoutMateriel(ActionEvent e) {
        changerScene("/AjoutMateriel.fxml", e);
    }

    private void ouvrirListeMateriels(ActionEvent e) {
        changerScene("/ModifierMateriel.fxml", e);
    }

    private void ouvrirAjoutFournisseur(ActionEvent e) {
        changerScene("/AjoutFournisseur.fxml", e);
    }

    private void ouvrirListeFournisseurs(ActionEvent e) {
        changerScene("/ModifierFournisseur.fxml", e);
    }

    private void ouvrirAjoutReservation(ActionEvent e) {
        changerScene("/AjoutReservation.fxml", e);
    }

    private void ouvrirListeReservations(ActionEvent e) {
        changerScene("/ModifierReservation.fxml", e);
    }

    private void ouvrirAjoutReservationsClient(ActionEvent e) {
        changerScene("/AjoutReservationClient.fxml", e);
    }

    private void ouvrirListeMaterielsClient(ActionEvent e) {
        changerScene("/ListeMaterielsClient.fxml", e);
    }

    private void ouvrirListeReservationsClient(ActionEvent e) {
        changerScene("/ListeReservationsClient.fxml", e);
    }

    private void changerScene(String cheminFXML, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void changerScene(String cheminFXML, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
