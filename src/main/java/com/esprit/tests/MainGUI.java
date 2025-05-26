package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the User interface
            URL location = getClass().getResource("/views/SelectionInterface.fxml");
            Parent root = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/User/AffichageProduitsUser.fxml"));

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
}