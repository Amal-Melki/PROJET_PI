package com.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilProduitDerive {

    @FXML
    void goToAjoutProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Produit Dérivé");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToModifierProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier les Produits Dérivés");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToCommandes(ActionEvent event) {
        // À implémenter selon vos besoins
        showAlert("Fonctionnalité à venir", "La gestion des commandes sera disponible dans une prochaine version.");
    }

    @FXML
    void goToListeProduits(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Produits Dérivés");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}