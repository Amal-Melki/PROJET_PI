package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AjouterEspaceClient {

    @FXML
    private TextField nomField, typeField, capaciteField, localisationField, prixField;

    @FXML
    private CheckBox disponibiliteCheck;

    @FXML
    private Button btnRetour;

    private final EspaceService espaceService = new EspaceService();

    @FXML
    private void ajouterEspace(ActionEvent event) {
        try {
            String nom = nomField.getText();
            String type = typeField.getText();
            int capacite = Integer.parseInt(capaciteField.getText());
            String localisation = localisationField.getText();
            double prix = Double.parseDouble(prixField.getText());
            boolean dispo = disponibiliteCheck.isSelected();

            Espace espace = new Espace(nom, type, capacite, localisation, prix, dispo);
            espaceService.ajouterEspace(espace);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Espace ajouté avec succès !");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Veuillez entrer des valeurs numériques valides pour capacité et prix.");
            alert.showAndWait();
        }
    }

    @FXML
    private void retourMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/views/MenuEspace.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
