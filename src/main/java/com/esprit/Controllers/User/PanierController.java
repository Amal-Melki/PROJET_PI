package com.esprit.Controllers.User;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.User.ServicePanier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class PanierController {

    @FXML
    private ListView<ProduitDerive> panierListView;
    @FXML
    private Label totalPriceLabel;

    private ServicePanier servicePanier = new ServicePanier();
    private ObservableList<ProduitDerive> produitsDansPanier;

    @FXML
    public void initialize() {
        produitsDansPanier = FXCollections.observableArrayList(servicePanier.getProduitsDansPanier());
        panierListView.setItems(produitsDansPanier);
        updateTotalPrice();

        panierListView.setCellFactory(param -> new javafx.scene.control.ListCell<ProduitDerive>() {
            @Override
            protected void updateItem(ProduitDerive item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " - " + String.format("%.2f TND", item.getPrix()));
                }
            }
        });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (ProduitDerive p : produitsDansPanier) {
            total += p.getPrix();
        }
        totalPriceLabel.setText(String.format("Total: %.2f TND", total));
    }

    @FXML
    private void handlePayment(ActionEvent event) {
        if (produitsDansPanier.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Votre panier est vide. Ajoutez des produits avant de payer.");
            return;
        }

        try {
            // **CHEMIN CLÉ** : Assurez-vous que ce chemin est correct par rapport à votre dossier 'resources'
            URL location = getClass().getResource("/views/User/PaymentForm.fxml");

            // Ajout d'une vérification explicite pour le cas où la ressource n'est pas trouvée
            if (location == null) {
                throw new IOException("FXML file not found at /views/User/PaymentForm.fxml. Check your project structure.");
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent paymentFormRoot = loader.load();
            PaymentFormController paymentFormController = loader.getController();

            // Passer le total au formulaire de paiement
            paymentFormController.setTotalAmount(getTotalPrice());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(paymentFormRoot);
            stage.setScene(scene);
            stage.setTitle("Formulaire de Paiement");
            stage.show();

        } catch (IOException e) {
            System.err.println("ERREUR (PanierController): Erreur lors du chargement du formulaire de paiement: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de paiement. " +
                    "Vérifiez que PaymentForm.fxml est dans 'src/main/resources/views/products/User' et que son contenu est valide. Détails: " + e.getMessage());
        }
    }

    private double getTotalPrice() {
        double total = 0;
        for (ProduitDerive p : produitsDansPanier) {
            total += p.getPrix();
        }
        return total;
    }

    @FXML
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
            System.err.println("ERREUR (PanierController): Erreur lors du retour à l'affichage des produits : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}