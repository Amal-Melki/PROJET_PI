package com.esprit.Controllers.User;

import com.esprit.services.produits.User.ServicePanier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class PaymentFormController {

    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryDateField;
    @FXML
    private TextField cvcField;
    @FXML
    private Label amountLabel;

    private double totalAmount;
    private ServicePanier servicePanier = new ServicePanier();

    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
        amountLabel.setText(String.format("%.2f TND", totalAmount));
    }

    @FXML
    private void processPayment(ActionEvent event) {
        String cardNumber = cardNumberField.getText();
        String expiryDate = expiryDateField.getText();
        String cvc = cvcField.getText();

        // 1. Validation des champs vides
        if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvc.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Veuillez remplir tous les champs du formulaire de paiement.");
            return;
        }

        // 2. Validation du numéro de carte (doit contenir uniquement des chiffres, 13 à 19 chiffres)
        if (!cardNumber.matches("\\d{13,19}")) {
            showAlert(Alert.AlertType.ERROR, "Format de Carte Invalide", "Le numéro de carte doit contenir entre 13 et 19 chiffres (sans espaces ni caractères spéciaux).");
            return;
        }

        // 3. Validation de la date d'expiration (MM/AA)
        if (!expiryDate.matches("^(0[1-9]|1[0-2])/([0-9]{2})$")) {
            showAlert(Alert.AlertType.ERROR, "Format de Date d'Expiration Invalide", "Le format de la date d'expiration doit être MM/AA (ex: 12/25) et le mois doit être entre 01 et 12.");
            return;
        }

        // 4. Validation du CVC (doit contenir uniquement des chiffres, 3 ou 4 chiffres)
        if (!cvc.matches("\\d{3,4}")) {
            showAlert(Alert.AlertType.ERROR, "Format CVC Invalide", "Le CVC doit contenir 3 ou 4 chiffres.");
            return;
        }

        // Si toutes les validations passent, procéder à la simulation de paiement
        showAlert(Alert.AlertType.INFORMATION, "Paiement Réussi", "Votre paiement de " + String.format("%.2f TND", totalAmount) + " a été traité avec succès !");

        // Vider le panier après un paiement réussi
        servicePanier.clearPanier();

        // Retour à l'affichage des produits
        retourToAffichageProduits(event);
    }

    @FXML
    private void cancelPayment(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Paiement Annulé", "Le paiement a été annulé. Vous pouvez continuer vos achats.");
        retourToPanier(event);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void retourToAffichageProduits(ActionEvent event) {
        try {
            URL location = getClass().getResource("/views/User/AffichageProduitsUser.fxml");
            Parent affichageProduitsRoot = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/User/AffichageProduitsUser.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(affichageProduitsRoot);
            stage.setScene(scene);
            stage.setTitle("Découvrez nos Produits");
            stage.show();
        } catch (IOException e) {
            System.err.println("ERREUR (PaymentFormController): Erreur lors du retour à l'affichage des produits : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void retourToPanier(ActionEvent event) {
        try {
            URL location = getClass().getResource("/views/products/Panier.fxml");
            Parent panierRoot = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/products/Panier.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(panierRoot);
            stage.setScene(scene);
            stage.setTitle("Mon Panier");
            stage.show();
        } catch (IOException e) {
            System.err.println("ERREUR (PaymentFormController): Erreur lors du retour au panier : " + e.getMessage());
            e.printStackTrace();
        }
    }
}