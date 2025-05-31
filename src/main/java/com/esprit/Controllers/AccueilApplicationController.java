package com.esprit.controllers;

import com.esprit.tests.MainGUI;
import com.esprit.tests.MainGUII;
import com.esprit.tests.MainProgGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AccueilApplicationController implements Initializable {
    @FXML
    private ImageView logoImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }
    public void ouvrirBlogs(ActionEvent event) {
        MainProgGUI.showWindow();
    }

    public void ouvrirProduits(ActionEvent event) {
        MainGUII.showWindow();
    }

    public void ouvrirMateriel(ActionEvent event) {
        MainGUI.showWindow();
    }
}
