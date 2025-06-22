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

public class Accueil {

    @FXML private Button btnAjoutMateriel;
    @FXML private Button btnListeMateriels;
    @FXML private Button btnAjoutFournisseur;
    @FXML private Button btnListeFournisseurs;
    @FXML private Button btnAjoutReservation;
    @FXML private Button btnListeReservations;
    @FXML private Button btnAjoutReservationClient;
    @FXML private Button btnListeMaterielsClient;
    @FXML private Button btnListeReservationsClient;//
    @FXML
    private ImageView logoImage;// ✅ nouveau bouton

    @FXML
    public void initialize() {
        btnAjoutMateriel.setOnAction(this::ouvrirAjoutMateriel);
        btnListeMateriels.setOnAction(this::ouvrirListeMateriels);
        btnAjoutFournisseur.setOnAction(this::ouvrirAjoutFournisseur);
        btnListeFournisseurs.setOnAction(this::ouvrirListeFournisseurs);
        btnAjoutReservation.setOnAction(this::ouvrirAjoutReservation);
        btnListeReservations.setOnAction(this::ouvrirListeReservations);
        btnAjoutReservationClient.setOnAction(this::ouvrirAjoutReservationsClient);
        btnListeMaterielsClient.setOnAction(this::ouvrirListeMaterielsClient);
        btnListeReservationsClient.setOnAction(this::ouvrirListeReservationsClient);
        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
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
        changerScene("/ListeMaterielsClient.fxml", e); // ✅ nouvelle vue
    }
    private void ouvrirListeReservationsClient(ActionEvent e) {
        changerScene("/ListeReservationsClient.fxml", e); // ✅ nouvelle vue
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
}
