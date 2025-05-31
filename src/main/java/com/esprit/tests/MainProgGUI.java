package com.esprit.tests;

import com.esprit.utils.DataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainProgGUI extends Application {

    public static void main(String[] args) {
        // Initialisation de la connexion DB au démarrage
        DataSource.getInstance().getConnection(); // Test la connexion
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlogRecuperer.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des Blogs");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (IOException e) {
            showAlert("Erreur FXML", "Fichier BlogRecuperer.fxml introuvable ou corrompu");
            e.printStackTrace();
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 🔹 Nouvelle méthode pour être appelée depuis une autre fenêtre sans relancer Application.launch()
    public static void showWindow() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainProgGUI.class.getResource("/BlogRecuperer.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Gestion des Blogs");
                stage.setScene(new Scene(root, 800, 600));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Impossible de charger la fenêtre BlogRecuperer.fxml");
                alert.showAndWait();
            }
        });
    }
}
