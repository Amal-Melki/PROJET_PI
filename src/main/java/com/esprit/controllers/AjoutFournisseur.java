package com.esprit.controllers;

import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
import com.esprit.utils.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class AjoutFournisseur implements Initializable {

    @FXML private TextField tfNom;
    @FXML private TextField tfTelephone;
    @FXML private TextField tfAdresse;
    @FXML private TextField tfEmail;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;
    @FXML
    private ImageView logoImage;
    private int codeVerification; // ✅ ajout du champ

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setStyle("-fx-background-color: #ff8cb3; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }

    @FXML
    void ajouterFournisseur() {
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

        ServiceFournisseur service = new ServiceFournisseur();
        List<Fournisseur> fournisseursExistants = service.recuperer();

        for (Fournisseur f : fournisseursExistants) {
            if (f.getNom().equalsIgnoreCase(nom)
                    && f.getEmail().equalsIgnoreCase(email)
                    && f.getTelephone().equalsIgnoreCase(telephone)) {
                showAlert(Alert.AlertType.WARNING, "Doublon", "Ce fournisseur existe déjà !");
                return;
            }
        }

        Fournisseur fournisseur = new Fournisseur(nom, email, telephone, adresse);
        service.ajouter(fournisseur);

        // ✅ Génération du code de vérification
        codeVerification = new Random().nextInt(900000) + 100000;

        // ✅ Envoi de l'e-mail
        EmailService.sendVerificationEmail(email, String.valueOf(codeVerification));

        // ✅ Message + nettoyage des champs
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

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
