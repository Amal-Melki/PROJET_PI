package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file and assign it to the root variable
Parent root = FXMLLoader.load(getClass().getResource("/products/AccueilProduitDerive.fxml"));

            primaryStage.setTitle("🏠 Accueil - Gestion Produits Dérivés");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du FXML:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
