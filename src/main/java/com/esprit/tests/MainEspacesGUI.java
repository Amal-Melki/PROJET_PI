package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class MainEspacesGUI extends Application {

    public static void main(String[] args) {
        launch(args); // Lance JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger la nouvelle interface TableauDeBordPrincipal.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/TableauDeBordPrincipal.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Apply stylesheet
            scene.getStylesheets().add(getClass().getClassLoader().getResource("styles/dashboard.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Evencia Event Planner");
            primaryStage.setMaximized(true); // Start maximized for better UX
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
            
            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de démarrage");
            alert.setHeaderText("Impossible de démarrer l'application");
            alert.setContentText("Une erreur est survenue: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode utilitaire pour charger un FXML dans une nouvelle fenêtre
    public static void loadFXML(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainEspacesGUI.class.getClassLoader().getResource("views/" + fxmlFileName));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface : " + e.getMessage());
            e.printStackTrace();
            
            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Impossible de charger l'interface");
            alert.setContentText("Une erreur est survenue lors du chargement de " + fxmlFileName + "\nDétails: " + e.getMessage());
            alert.showAndWait();
        }
    }
}