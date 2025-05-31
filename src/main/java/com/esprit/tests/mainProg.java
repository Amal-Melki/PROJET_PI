package com.esprit.tests;

import com.esprit.services.CommentaireService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.util.logging.Level;
import java.util.logging.Logger;

public class mainProg extends Application {

    private static final Logger LOGGER = Logger.getLogger(mainProg.class.getName());
//tasna3 fekret les crud NB : lezemni ndhabet ml tasmiet s7a7 wile le
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlogRecuperer.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Gestion de Blog");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur au démarrage de l'application :", e);
        }
    }


    public static void main(String[] args) {
        launch(args);
        CommentaireService c = new CommentaireService();
    }
}
