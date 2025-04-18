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

public class ModifierFournisseurFormulaire implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfTelephone;

    @FXML
    private TextField tfAdresse;

    @FXML
    private TextField tfEmail;

    @FXML
    private Button btnEnregistrer;

    private Fournisseur fournisseur; // L’objet à modifier

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Style du bouton
        btnEnregistrer.setStyle(
                "-fx-background-color: #ff8cb3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;"
        );
    }

    // Méthode appelée par le contrôleur principal
    public void initData(Fournisseur f) {
        this.fournisseur = f;
        tfNom.setText(f.getNom());
        tfTelephone.setText(f.getTelephone());
        tfAdresse.setText(f.getAdresse());
        tfEmail.setText(f.getEmail());
    }

    @FXML
    void modifierFournisseur() {
        String nom = tfNom.getText().trim();
        String telephone = tfTelephone.getText().trim();
        String adresse = tfAdresse.getText().trim();
        String email = tfEmail.getText().trim();

        if (nom.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!telephone.matches("\\d{8,15}")) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Le numéro doit contenir uniquement des chiffres (entre 8 et 15).");
            return;
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        // Mise à jour des valeurs dans l'objet
        fournisseur.setNom(nom);
        fournisseur.setTelephone(telephone);
        fournisseur.setAdresse(adresse);
        fournisseur.setEmail(email);

        // Enregistrer la modification dans la base
        ServiceFournisseur service = new ServiceFournisseur();
        service.modifier(fournisseur);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur modifié avec succès !");
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
