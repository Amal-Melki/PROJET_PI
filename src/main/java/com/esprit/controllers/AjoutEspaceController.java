package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class AjoutEspaceController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField capaciteField;
    @FXML
    private TextField localisationField;
    @FXML
    private TextField prixField;
    @FXML
    private CheckBox disponibiliteCheck;

    private EspaceService espaceService = new EspaceService();

    @FXML
    private void retourMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuEspace.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ajouterEspace(ActionEvent event) {
        String nom = nomField.getText();
        String type = typeField.getText();
        String capaciteStr = capaciteField.getText();
        String localisation = localisationField.getText();
        String prixStr = prixField.getText();
        boolean disponibilite = disponibiliteCheck.isSelected();

        if (nom.isEmpty() || type.isEmpty() || capaciteStr.isEmpty() || localisation.isEmpty() || prixStr.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            int capacite = Integer.parseInt(capaciteStr);
            double prix = Double.parseDouble(prixStr);

            Espace espace = new Espace(nom, type, capacite, localisation, prix, disponibilite);
            espaceService.add(espace);

            showAlert(AlertType.INFORMATION, "Succès", "Espace ajouté", "L'espace a été ajouté avec succès.");

            // Réinitialisation des champs
            nomField.clear();
            typeField.clear();
            capaciteField.clear();
            localisationField.clear();
            prixField.clear();
            disponibiliteCheck.setSelected(false);

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "Format invalide",
                    "La capacité doit être un nombre entier et le prix doit être un nombre décimal.");
        }
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}