package com.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilProduitDerive {

    @FXML
    private Button btnAjoutProduit;
    @FXML
    private Button btnModifierProduit;
    @FXML
    private Button btnSupprimerProduit;
    @FXML
    private Button btnListeProduits;
    @FXML
    private Button btnCommandes; // bien synchronisé avec fx:id dans le FXML

    @FXML
    public void initialize() {
        btnAjoutProduit.setOnAction(this::ouvrirAjoutProduit);
        btnModifierProduit.setOnAction(this::ouvrirModifierProduit);
        btnSupprimerProduit.setOnAction(this::ouvrirSupprimerProduit);
        btnListeProduits.setOnAction(this::ouvrirListeProduits);
        btnCommandes.setOnAction(this::ouvrirCommandes);
    }

    private void ouvrirAjoutProduit(ActionEvent e) {
        changerScene("/com/esprit/Views/AjoutProduitDerive.fxml", e);
    }

    private void ouvrirModifierProduit(ActionEvent e) {
        changerScene("/com/esprit/Views/ModifierProduitDerive.fxml", e);
    }

    private void ouvrirSupprimerProduit(ActionEvent e) {
        changerScene("/com/esprit/Views/SupprimerProduitDerive.fxml", e);
    }

    private void ouvrirListeProduits(ActionEvent e) {
        changerScene("/com/esprit/Views/ListeProduitsDerives.fxml", e); // ou autre si nécessaire
    }

    private void ouvrirCommandes(ActionEvent e) {
        changerScene("/com/esprit/Views/CommandesProduitDerive.fxml", e); // à créer si besoin
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
