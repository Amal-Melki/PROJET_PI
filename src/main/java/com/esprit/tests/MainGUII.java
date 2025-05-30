package com.esprit.tests;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class MainGUII extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL location = getClass().getResource("/views/SelectionInterface.fxml");
            Parent root = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/SelectionInterface.fxml"));

            primaryStage.setTitle("Découvrez nos Produits");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du FXML de l'interface utilisateur:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // 🔹 Nouvelle méthode pour l’ouverture sans relancer launch()
    public static void showWindow() {
        Platform.runLater(() -> {
            try {
                URL location = MainGUII.class.getResource("/views/SelectionInterface.fxml");
                Parent root = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found"));

                Stage stage = new Stage();
                stage.setTitle("Découvrez nos Produits");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                System.err.println("Erreur lors de l'ouverture de l'interface Produits : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
