package com.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SelectionInterfaceController {

    public void switchToAdminInterface(ActionEvent event) throws IOException {
        // Chemin corrigé pour l'interface Admin
        // D'après ta structure, AccueilProduitDerive.fxml est dans views/products/
        Parent adminRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/products/AccueilProduitDerive.fxml"),
                "FXML file not found at /views/products/AccueilProduitDerive.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(adminRoot);
        stage.setScene(scene);
        stage.setTitle("Accueil - Gestion Produits Dérivés (Admin)");
        stage.show();
    }

    public void switchToUserInterface(ActionEvent event) throws IOException {
        // Chemin corrigé pour l'interface Utilisateur
        // D'après ta structure, AffichageProduitsUser.fxml est dans views/User/
        Parent userRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/User/AffichageProduitsUser.fxml"),
                "FXML file not found at /views/User/AffichageProduitsUser.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(userRoot);
        stage.setScene(scene);
        stage.setTitle("Découvrez nos Produits (Utilisateur)");
        stage.show();
    }
}