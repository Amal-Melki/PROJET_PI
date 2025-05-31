package com.esprit.controllers.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // Ajoutez cet import
import java.util.Objects; // Ajoutez cet import

public class AccueilProduitDerive {

    @FXML
    private Button btnAjoutProduit;

    @FXML
    private Button btnListeProduits;

    @FXML
    public void initialize() {
        btnAjoutProduit.setOnAction(this::ouvrirAjoutProduit);
        btnListeProduits.setOnAction(this::ouvrirListeProduits);
    }

    @FXML
    private void ouvrirAjoutProduit(ActionEvent event) {
        changerScene("/views/products/AjoutProduitDerive.fxml", event);
    }

    @FXML
    private void ouvrirListeProduits(ActionEvent event) {
        changerScene("/views/products/ListeProduitDerive.fxml", event);
    }

    // NOUVELLE MÉTHODE POUR LE BOUTON "RETOUR"
    // Cette méthode sera appelée lorsque le bouton "Retour" dans AccueilProduitDerive.fxml est cliqué.
    @FXML
    public void retourToSelection(ActionEvent event) {
        try {
            // Le chemin vers SelectionInterface.fxml est crucial ici.
            // Il doit correspondre à l'emplacement réel de SelectionInterface.fxml dans vos ressources.
            URL location = getClass().getResource("/views/SelectionInterface.fxml");

            // Utilisez Objects.requireNonNull pour lancer une exception plus claire si le FXML n'est pas trouvé.
            Parent selectionRoot = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/SelectionInterface.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(selectionRoot));
            stage.setTitle("Choisissez votre interface"); // Définissez un titre pour l'écran de sélection
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à l'interface de sélection : " + e.getMessage());
            e.printStackTrace();
        }
    }


    // La méthode changerScene existante, juste légèrement améliorée pour le débogage.
    private void changerScene(String cheminFXML, ActionEvent event) {
        try {
            // Utilisez Objects.requireNonNull pour lancer une exception plus claire si le FXML n'est pas trouvé.
            URL location = getClass().getResource(cheminFXML);
            Parent root = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at " + cheminFXML));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            // Note: Le titre de la nouvelle scène n'est pas défini ici, car la signature de la méthode n'a pas été changée.
            stage.show();

        } catch (IOException ex) {
            System.err.println("Erreur lors du changement de scène vers " + cheminFXML + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}