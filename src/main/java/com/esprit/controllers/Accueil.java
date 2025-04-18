package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Accueil {

    @FXML
    private Button btnAjoutMateriel;

    @FXML
    private Button btnListeMateriels;

    @FXML
    private Button btnAjoutFournisseur;

    @FXML
    private Button btnListeFournisseurs;

    @FXML
    private Button btnAjoutReservation;

    @FXML
    private Button btnListeReservations;

    @FXML
    public void initialize() {
        btnAjoutMateriel.setOnAction(this::ouvrirAjoutMateriel);
        btnListeMateriels.setOnAction(this::ouvrirListeMateriels);
        btnAjoutFournisseur.setOnAction(this::ouvrirAjoutFournisseur);
        btnListeFournisseurs.setOnAction(this::ouvrirListeFournisseurs);
        btnAjoutReservation.setOnAction(this::ouvrirAjoutReservation);
        btnListeReservations.setOnAction(this::ouvrirListeReservations);
    }

    private void ouvrirAjoutMateriel(ActionEvent e) {
        chargerInterface("/AjoutMateriel.fxml", "Ajout Matériel");
    }

    private void ouvrirListeMateriels(ActionEvent e) {
        chargerInterface("/ModifierMateriel.fxml", "Liste des Matériels");
    }

    private void ouvrirAjoutFournisseur(ActionEvent e) {
        chargerInterface("/AjoutFournisseur.fxml", "Ajout Fournisseur");
    }

    private void ouvrirListeFournisseurs(ActionEvent e) {
        chargerInterface("/ModifierFournisseur.fxml", "Liste des Fournisseurs");
    }

    private void ouvrirAjoutReservation(ActionEvent e) {
        chargerInterface("/AjoutReservation.fxml", "Ajout Réservation");
    }

    private void ouvrirListeReservations(ActionEvent e) {
        chargerInterface("/ModifierReservation.fxml", "Liste des Réservations");
    }

    private void chargerInterface(String cheminFXML, String titreFenetre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titreFenetre);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
