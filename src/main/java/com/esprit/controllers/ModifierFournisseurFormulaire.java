package com.esprit.controllers;

import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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

    @FXML
    private Button btnRetour;

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

        // Mise à jour de l'objet
        fournisseur.setNom(nom);
        fournisseur.setTelephone(telephone);
        fournisseur.setAdresse(adresse);
        fournisseur.setEmail(email);

        // Enregistrement dans la base
        ServiceFournisseur service = new ServiceFournisseur();
        service.modifier(fournisseur);

        // Affichage confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Fournisseur modifié avec succès !");
        alert.showAndWait();


    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFournisseur.fxml"));
            Parent root = loader.load();

            // Remplacer la scène actuelle
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
