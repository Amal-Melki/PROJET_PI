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

// Ajouts pour l'affichage détaillé dans la ListView si vous voulez
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;


public class PanierController {

    @FXML
    private ListView<ProduitDerive> panierListView;
    @FXML
    private Label totalPriceLabel;

    private ServicePanier servicePanier = new ServicePanier();
    private ObservableList<ProduitDerive> produitsDansPanier;

    @FXML
    public void initialize() {
        // Au lieu de FXCollections.observableArrayList(servicePanier.getProduitsDansPanier());
        // Nous allons charger et afficher les éléments de manière plus détaillée
        loadPanierItems();
    }

    private void loadPanierItems() {
        produitsDansPanier = FXCollections.observableArrayList(servicePanier.getProduitsDansPanier());
        panierListView.setItems(produitsDansPanier);
        updateTotalPrice();

        panierListView.setCellFactory(param -> new javafx.scene.control.ListCell<ProduitDerive>() {
            private final HBox hbox = new HBox(10);
            private final Label nameLabel = new Label();
            private final Label priceLabel = new Label();
            private final Button removeButton = new Button("X");

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPadding(new Insets(5, 0, 5, 0));
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff8fb3;");
                removeButton.setStyle("-fx-background-color: #f06292; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3; -fx-font-size: 10px;");

                HBox.setHgrow(nameLabel, Priority.ALWAYS); // Permet au nom de prendre l'espace
                hbox.getChildren().addAll(nameLabel, priceLabel, removeButton);

                removeButton.setOnAction(event -> {
                    ProduitDerive itemToRemove = getItem();
                    if (itemToRemove != null) {
                        servicePanier.removeProduitFromPanier(itemToRemove);
                        produitsDansPanier.remove(itemToRemove); // Mise à jour de l'ObservableList
                        updateTotalPrice();
                        showAlert(Alert.AlertType.INFORMATION, "Suppression", itemToRemove.getNom() + " a été retiré du panier.");
                    }
                });
            }

            @Override
            protected void updateItem(ProduitDerive item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getNom());
                    priceLabel.setText(String.format("%.2f TND", item.getPrix()));
                    setGraphic(hbox);
                }
            }
        });
    }


    private void updateTotalPrice() {
        totalPriceLabel.setText(String.format("Total: %.2f TND", servicePanier.calculerTotalPanier()));
    }

    @FXML
    private void handlePayment(ActionEvent event) {
        if (servicePanier.getProduitsDansPanier().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Votre panier est vide. Ajoutez des produits avant de payer.");
            return;
        }

        try {
            // **CHEMIN CLÉ** : Assurez-vous que ce chemin est correct par rapport à votre dossier 'resources'
            // D'après votre FXML Panier.fxml, votre PaymentForm.fxml est dans /views/User/
            URL location = getClass().getResource("/views/User/PaymentForm.fxml");

            if (location == null) {
                throw new IOException("FXML file not found at /views/User/PaymentForm.fxml. Check your project structure.");
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent paymentFormRoot = loader.load();
            PaymentFormController paymentFormController = loader.getController();

            // Passer le total au formulaire de paiement
            paymentFormController.setTotalAmount(servicePanier.calculerTotalPanier());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(paymentFormRoot);
            stage.setScene(scene);
            stage.setTitle("Formulaire de Paiement");
            stage.show();

        } catch (IOException e) {
            System.err.println("ERREUR (PanierController): Erreur lors du chargement du formulaire de paiement: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de paiement. " +
                    "Vérifiez que PaymentForm.fxml est dans 'src/main/resources/views/User' et que son contenu est valide. Détails: " + e.getMessage());
        }
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