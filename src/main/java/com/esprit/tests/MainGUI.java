package com.esprit.tests;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
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

    public static void main(String[] args) {
        launch(args);
    }

    // 🔹 Nouvelle méthode d’ouverture depuis un autre contrôleur
    public static void showWindow() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainGUI.class.getResource("/Accueil.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("🏠 Accueil - Gestion des Ressources");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                System.err.println("Erreur lors de l'ouverture de l'interface Gestion du Matériel : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
