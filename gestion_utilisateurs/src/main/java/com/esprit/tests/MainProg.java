package com.esprit.tests;

import com.esprit.utils.DatabaseUtil;
import com.sun.source.tree.ParenthesizedTree;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MainProg extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Vérifier la connexion à la base de données
            try (Connection connection = DatabaseUtil.getConnection()) {
                System.out.println("Connexion à la base de données réussie.");
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
                e.printStackTrace();
                showDatabaseErrorAndExit(primaryStage, e.getMessage());
                return;
            }

            // Charger l'écran de connexion
//            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Authentification.fxml"));

            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("/ressources/Dashboard.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Connexion - Gestion Utilisateurs");

            // Optionnel: ajouter une icône à l'application
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de l'interface: " + e.getMessage());
        }
    }

    private void showDatabaseErrorAndExit(Stage stage, String errorMessage) {
        try {
            // Créer une scène d'erreur simple
            javafx.scene.layout.VBox errorBox = new javafx.scene.layout.VBox(10);
            errorBox.setAlignment(javafx.geometry.Pos.CENTER);
            errorBox.setPadding(new javafx.geometry.Insets(20));

            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Erreur de connexion à la base de données");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(
                    "Impossible de se connecter à la base de données. Vérifiez que MySQL est en cours d'exécution et que la base de données existe.");
            messageLabel.setWrapText(true);

            javafx.scene.control.Label detailsLabel = new javafx.scene.control.Label("Détails: " + errorMessage);
            detailsLabel.setWrapText(true);

            javafx.scene.control.Button exitButton = new javafx.scene.control.Button("Quitter");
            exitButton.setOnAction(e -> System.exit(1));

            errorBox.getChildren().addAll(titleLabel, messageLabel, detailsLabel, exitButton);

            Scene errorScene = new Scene(errorBox, 500, 300);
            stage.setScene(errorScene);
            stage.setTitle("Erreur de connexion");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}