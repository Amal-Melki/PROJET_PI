package com.esprit.Controllers.User;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.User.ServicePanier;
<<<<<<< HEAD
=======
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
<<<<<<< HEAD
import javafx.scene.control.Button; // Needed for quantity buttons
import javafx.scene.layout.HBox;    // Needed for dynamic item layout
import javafx.scene.layout.Priority; // Needed for dynamic item layout
import javafx.scene.layout.VBox;    // New VBox to hold dynamic cart items
import javafx.stage.Stage;

import javafx.geometry.Insets; // IMPORTANT: Ensure this import is present for Insets
import javafx.geometry.Pos;    // IMPORTANT: Ensure this import is present for Pos

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

=======
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

>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727

public class PanierController {

    @FXML
<<<<<<< HEAD
    private VBox cartItemsVBox; // This will hold our dynamically added product HBoxes
=======
    private ListView<ProduitDerive> panierListView;
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
    @FXML
    private Label totalPriceLabel;

    private ServicePanier servicePanier = new ServicePanier();
<<<<<<< HEAD

    @FXML
    public void initialize() {
=======
    private ObservableList<ProduitDerive> produitsDansPanier;

    @FXML
    public void initialize() {
        // Au lieu de FXCollections.observableArrayList(servicePanier.getProduitsDansPanier());
        // Nous allons charger et afficher les éléments de manière plus détaillée
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
        loadPanierItems();
    }

    private void loadPanierItems() {
<<<<<<< HEAD
        cartItemsVBox.getChildren().clear(); // Clear existing items

        Map<ProduitDerive, Integer> panierItems = servicePanier.getPanierItemsWithQuantities();

        if (panierItems.isEmpty()) {
            Label emptyCartLabel = new Label("Votre panier est vide.");
            emptyCartLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            cartItemsVBox.getChildren().add(emptyCartLabel);
        } else {
            for (Map.Entry<ProduitDerive, Integer> entry : panierItems.entrySet()) {
                ProduitDerive produit = entry.getKey();
                int quantity = entry.getValue();
                cartItemsVBox.getChildren().add(createCartItemHBox(produit, quantity));
            }
        }
        updateTotalPrice();
    }

    private HBox createCartItemHBox(ProduitDerive produit, int quantity) {
        HBox hbox = new HBox(10);
        // Corrected padding: Ensure Insets is imported. This syntax is standard JavaFX.
        hbox.setPadding(new Insets(5, 10, 5, 10));
        hbox.setAlignment(Pos.CENTER_LEFT); // Use Pos from javafx.geometry
        hbox.setStyle("-fx-background-color: #FFF0F5; -fx-border-color: #F8BBD0; -fx-border-radius: 5; -fx-border-width: 0.5;"); // Light pink background for items

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS); // Allow name to take available space

        Label priceLabel = new Label(String.format("%.2f TND", produit.getPrix()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff8fb3;");

        // Quantity controls
        Button removeQtyButton = new Button("−");
        removeQtyButton.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 3; -fx-font-size: 10px;");
        Label quantityLabel = new Label(String.valueOf(quantity));
        quantityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #444444; -fx-min-width: 25px; -fx-alignment: center;");
        Button addQtyButton = new Button("+");
        addQtyButton.setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #388E3C; -fx-font-weight: bold; -fx-background-radius: 3; -fx-font-size: 10px;");

        Button deleteItemButton = new Button("X");
        deleteItemButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3; -fx-font-size: 10px;");

        // Action for adding quantity
        addQtyButton.setOnAction(e -> {
            int currentQty = servicePanier.getProduitQuantity(produit);
            servicePanier.setProduitQuantity(produit, currentQty + 1);
            loadPanierItems(); // Reload the whole list to update quantities and total
        });

        // Action for removing quantity
        removeQtyButton.setOnAction(e -> {
            int currentQty = servicePanier.getProduitQuantity(produit);
            if (currentQty > 1) { // Decrement if > 1
                servicePanier.setProduitQuantity(produit, currentQty - 1);
                loadPanierItems();
            } else { // If quantity is 1, remove the item completely
                servicePanier.removeProduitFromPanier(produit);
                loadPanierItems();
                showAlert(Alert.AlertType.INFORMATION, "Suppression", produit.getNom() + " a été retiré du panier.");
            }
        });

        // Action for deleting item completely (the 'X' button)
        deleteItemButton.setOnAction(event -> {
            servicePanier.removeProduitFromPanier(produit);
            loadPanierItems();
            showAlert(Alert.AlertType.INFORMATION, "Suppression", produit.getNom() + " a été retiré du panier.");
        });

        // Add elements to HBox
        hbox.getChildren().addAll(nameLabel, priceLabel, removeQtyButton, quantityLabel, addQtyButton, deleteItemButton);

        return hbox;
=======
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
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
    }


    private void updateTotalPrice() {
        totalPriceLabel.setText(String.format("Total: %.2f TND", servicePanier.calculerTotalPanier()));
    }

    @FXML
    private void handlePayment(ActionEvent event) {
<<<<<<< HEAD
        // You should now check if the map is empty, not just the list
        if (servicePanier.getPanierItemsWithQuantities().isEmpty()) {
=======
        if (servicePanier.getProduitsDansPanier().isEmpty()) {
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Votre panier est vide. Ajoutez des produits avant de payer.");
            return;
        }

        try {
<<<<<<< HEAD
=======
            // **CHEMIN CLÉ** : Assurez-vous que ce chemin est correct par rapport à votre dossier 'resources'
            // D'après votre FXML Panier.fxml, votre PaymentForm.fxml est dans /views/User/
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
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