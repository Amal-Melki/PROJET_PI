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
    private Button btnListeProduits;


    @FXML
    private Button btnPanier;

    @FXML
    public void initialize() {
        btnAjoutProduit.setOnAction(this::ouvrirAjoutProduit);
        btnListeProduits.setOnAction(this::ouvrirListeProduits);
        btnPanier.setOnAction(this::ouvrirPanier);
    }

    @FXML
    private void ouvrirAjoutProduit(ActionEvent event) {

        changerScene("/products/AjoutProduitDerive.fxml", event);
    }

    @FXML
    private void ouvrirListeProduits(ActionEvent event) {
        changerScene("/products/ListeProduitDerive.fxml", event);
    }

    @FXML
    private void ouvrirPanier(ActionEvent event) {
        changerScene("/products/Panier.fxml", event);
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
