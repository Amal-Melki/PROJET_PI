package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainEspacesGUI extends Application {

    public static void main(String[] args) {
        launch(args); // Lance JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger l'interface principale MenuEspace.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuEspace.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des Espaces");
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Erreur au démarrage : " + e.getMessage());

        }
    }

    // Méthode pour ouvrir n'importe quelle interface FXML
    public static void loadFXML(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainEspacesGUI.class.getResource("/com/esprit/Resources/" + fxmlFileName));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'interface : " + e.getMessage());

        }
    }
}