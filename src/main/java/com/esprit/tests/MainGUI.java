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
            // ⚠ Ancienne interface : AjoutMateriel
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutMateriel.fxml"));

            // ✅ Nouvelle interface : ModifierMateriel (liste avec boutons)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMateriel.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Liste des Matériels");
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Erreur au démarrage : " + e.getMessage());
        }
    }
}
