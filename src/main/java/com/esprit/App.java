package com.esprit;

import com.esprit.API.ApiServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Démarrer l'API locale en arrière-plan
        new Thread(() -> {
            try {
                ApiServer.start();
            } catch (IOException e) {
                System.err.println("Erreur lors du démarrage de l'API :");
                e.printStackTrace();
            }
        }).start();

        // Charger l'interface JavaFX
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        stage.setTitle("Blog Manager");
        stage.setScene(new Scene(root, 800, 600)); // Dimensions par défaut
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
