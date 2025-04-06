package com.esprit.controllers;

import com.esprit.models.Personne;
import com.esprit.services.PersonneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AjoutPersonne {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;

    @FXML
    void addPerson(ActionEvent event) throws IOException {
        PersonneService ps = new PersonneService();
        ps.ajouter(new Personne(tfNom.getText(), tfPrenom.getText()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Personne ajoutée");
        alert.show();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichePersonne.fxml"));
        Parent root = loader.load();
        tfNom.getScene().setRoot(root);

        AffichePersonne ap = loader.getController();
        ap.setLbNom(tfNom.getText());
        ap.setLbPrenom(tfPrenom.getText());
    }

}
