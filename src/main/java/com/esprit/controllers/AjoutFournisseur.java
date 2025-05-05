package com.esprit.controllers;

import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AjoutFournisseur implements Initializable {

    @FXML private TextField tfNom;
    @FXML private TextField tfTelephone;
    @FXML private TextField tfAdresse;
    @FXML private TextField tfEmail;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setStyle("-fx-background-color: #ff8cb3; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
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

        sendConfirmationEmail(email, nom);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur ajouté avec succès !");
        tfNom.clear();
        tfTelephone.clear();
        tfAdresse.clear();
        tfEmail.clear();
    }

    private void sendConfirmationEmail(String recipientEmail, String nomFournisseur) {
        final String senderEmail = "votre.email@gmail.com";
        final String senderPassword = "votre_mot_de_passe_app"; // utilisez un mot de passe d'application Gmail

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Bienvenue chez notre plateforme");
            message.setText("Bonjour " + nomFournisseur + ",\n\nVotre enregistrement a été validé avec succès.\n\nCordialement.");

            Transport.send(message);
        } catch (MessagingException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Email", "L'envoi de l'email a échoué : " + e.getMessage());
        }
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
