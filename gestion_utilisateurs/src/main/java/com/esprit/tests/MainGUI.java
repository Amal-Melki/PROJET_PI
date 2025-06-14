package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args); // Lance JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // ⚠ Ancienne interface : Ajout Matériel
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutMateriel.fxml"));

            // ⚠ Liste des matériels avec bouton "Modifier"
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMateriel.fxml"));

            // ⚠ Formulaire d’ajout de fournisseur
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutFournisseur.fxml"));

            // ⚠ Liste des fournisseurs avec bouton Modifier
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFournisseur.fxml"));

            // ⚠ Formulaire d'ajout de réservation
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReservation.fxml"));

            // ⚠ Liste des réservations avec bouton Modifier
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReservation.fxml"));

            // ✅ Interface d'accueil principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("🏠 Accueil - Gestion des Ressources");
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
