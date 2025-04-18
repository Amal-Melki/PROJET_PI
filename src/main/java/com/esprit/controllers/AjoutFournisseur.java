package com.esprit.controllers;

import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AjoutFournisseur implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfTelephone;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfEmail;

    @FXML
    private Button btnAjouter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Style du bouton
        btnAjouter.setStyle(
                "-fx-background-color: #ff8cb3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;"
        );
    }

    @FXML
    void ajouterFournisseur() {
        String nom = tfNom.getText().trim();
        String telephone = tfTelephone.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String email = tfEmail.getText().trim();

        // Vérification des champs
        if (nom.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification téléphone : chiffres uniquement, entre 8 et 15
        if (!telephone.matches("\\d{8,15}")) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Le numéro doit contenir uniquement des chiffres (entre 8 et 15).");
            return;
        }

        // Vérification email basique
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        Fournisseur fournisseur = new Fournisseur(nom, email, telephone, adresse);
        ServiceFournisseur service = new ServiceFournisseur();
        service.ajouter(fournisseur);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur ajouté avec succès !");

        tfNom.clear();
        tfTelephone.clear();
        tfAdresse.clear();
        tfEmail.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
