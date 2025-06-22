package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChoixBlogController {

    @FXML
    void ouvrirBlogRecuperer(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/BlogRecuperer.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Recuperer Blog");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void ouvrirClientBlog(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ClientBlogView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Client Blog");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
