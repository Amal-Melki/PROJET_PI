package com.esprit.tests;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

    public class MainEspacesGUI extends Application {

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/AjouteEspace.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajoute Espace");
            primaryStage.show();
        }
    }





