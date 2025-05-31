package com.esprit.controllers.User;

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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.awt.Desktop; // Import nécessaire pour ouvrir le PDF automatiquement (si vous le voulez dans le pop-up)

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

        // --- SIMULATION DU PAIEMENT ---
        boolean paymentSuccessful = true; // Pour l'exemple, on considère toujours le paiement comme réussi après validation.

        if (paymentSuccessful) {
            // AFFICHER LE POP-UP DE CONFIRMATION EN PREMIER
            // Le showAndWait() est CRUCIAL ici. Il bloque ce thread jusqu'à ce que le pop-up soit fermé.
            // Cela garantit que le panier n'est vidé et que la redirection n'a lieu qu'après l'interaction avec le pop-up.
            showConfirmationPopup(event);

            // Une fois le pop-up fermé (après que l'utilisateur a cliqué sur "Fermer" ou sur le bouton de téléchargement)
            // Vider le panier
            servicePanier.clearPanier();
            showAlert(Alert.AlertType.INFORMATION, "Paiement Réussi", "Votre paiement de " + String.format("%.2f TND", totalAmount) + " a été traité avec succès !");

            // Ensuite, rediriger vers l'affichage des produits
            retourToAffichageProduits(event);

        } else {
            showAlert(Alert.AlertType.ERROR, "Paiement Échoué", "Votre paiement n'a pas pu être traité. Veuillez vérifier vos informations ou réessayer plus tard.");
        }
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

    // MÉTHODE POUR AFFICHER LE POP-UP DE CONFIRMATION
    private void showConfirmationPopup(ActionEvent event) {
        try {
            // VERIFIEZ ATTENTIVEMENT CE CHEMIN FXML. C'est la source la plus fréquente de problèmes.
            // Il doit correspondre à l'emplacement physique de votre fichier ConfirmationPaiementPopup.fxml
            // dans src/main/resources/views/User/
            URL location = getClass().getResource("/views/User/ConfirmationPaiementPopup.fxml");

            if (location == null) {
                // Ce message d'erreur est très important pour le débogage
                throw new IOException("FXML file not found at /views/User/ConfirmationPaiementPopup.fxml. Double-check your project structure and file path.");
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();

            ConfirmationPaiementPopupController popupController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Rend le pop-up bloquant
            stage.setTitle("Paiement Confirmé");
            stage.setScene(new Scene(root));
            popupController.setDialogStage(stage); // Permet au contrôleur du pop-up de fermer sa propre fenêtre
            stage.showAndWait(); // BLOQUE L'EXÉCUTION JUSQU'À CE QUE LE POP-UP SOIT FERMÉ
            // Le code suivant (vider le panier, redirection) ne s'exécute qu'après que le pop-up a été fermé par l'utilisateur.

        } catch (IOException e) {
            System.err.println("ERREUR GRAVE (PaymentFormController): Erreur lors de l'affichage du pop-up de confirmation : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Interne", "Impossible d'afficher la confirmation de paiement. " +
                    "Vérifiez que ConfirmationPaiementPopup.fxml est dans 'src/main/resources/views/User' et que son contenu est valide. Détails: " + e.getMessage());
        }
    }

    // URL CORRECTE POUR BASCULER APRÈS LE PAIEMENT (vers AffichageProduitsUser.fxml)
    private void retourToAffichageProduits(ActionEvent event) {
        try {
            // Ce chemin doit pointer vers votre page d'affichage des produits.
            // Si votre fichier est dans src/main/resources/views/User/AffichageProduitsUser.fxml
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
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à la page des produits.");
        }
    }

    // URL CORRECTE POUR RETOURNER AU PANIER (vers Panier.fxml)
    private void retourToPanier(ActionEvent event) {
        try {
            // Ce chemin doit pointer vers votre page Panier.fxml
            // Si votre fichier est dans src/main/resources/views/products/Panier.fxml (comme dans votre code initial)
            URL location = getClass().getResource("/views/products/Panier.fxml"); // VOTRE CHEMIN INITIAL

            Parent panierRoot = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/products/Panier.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(panierRoot);
            stage.setScene(scene);
            stage.setTitle("Mon Panier");
            stage.show();
        } catch (IOException e) {
            System.err.println("ERREUR (PaymentFormController): Erreur lors du retour au panier : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner au panier.");
        }
    }
}