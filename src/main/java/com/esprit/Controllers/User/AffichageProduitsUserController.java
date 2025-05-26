package com.esprit.Controllers.User;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.User.ServiceProduitDeriveUser;
import com.esprit.services.produits.User.ServicePanier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.sql.SQLException; // Import pour SQLException

public class AffichageProduitsUserController {

    @FXML
    private FlowPane flowPaneProduits;
    @FXML
    private ImageView logoImageView;
    @FXML
    private TextField searchField; // Champ pour la recherche par nom
    @FXML
    private TextField categoryFilterField; // Nouveau champ pour le filtrage par catégorie

    private final ServiceProduitDeriveUser produitService = new ServiceProduitDeriveUser();
    private final ServicePanier servicePanier = new ServicePanier();

    private List<ProduitDerive> tousLesProduits; // Pour stocker tous les produits non filtrés
    private static final String DEFAULT_PRODUCT_IMAGE_PATH = "/images/Mug4.jpg";

    @FXML
    public void initialize() {
        try {
            tousLesProduits = produitService.obtenirTousLesProduits();
            afficherProduits(tousLesProduits);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement initial des produits : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger les produits. Veuillez réessayer.");
        }

        // Listener pour la recherche par nom "en direct" ou la réinitialisation
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() && categoryFilterField.getText().isEmpty()) {
                afficherProduits(tousLesProduits);
            }
        });
        // Action sur appui sur Entrée dans le champ de recherche par nom
        searchField.setOnAction(event -> handleSearch(new ActionEvent()));

        // Listener pour le filtre par catégorie "en direct" ou la réinitialisation
        categoryFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() && searchField.getText().isEmpty()) {
                afficherProduits(tousLesProduits);
            }
        });
        // Action sur appui sur Entrée dans le champ de filtre par catégorie
        categoryFilterField.setOnAction(event -> handleFilterByCategory(new ActionEvent()));
    }

    private void afficherProduits(List<ProduitDerive> produits) {
        flowPaneProduits.getChildren().clear();

        if (produits != null && !produits.isEmpty()) {
            for (ProduitDerive produit : produits) {
                flowPaneProduits.getChildren().add(creerBoxProduit(produit));
            }
        } else {
            Label aucunProduitLabel = new Label("Aucun produit trouvé.");
            aucunProduitLabel.setFont(Font.font("Segoe UI", 16));
            aucunProduitLabel.setStyle("-fx-text-fill: gray;");
            flowPaneProduits.getChildren().add(aucunProduitLabel);
        }
    }

    private VBox creerBoxProduit(ProduitDerive produit) {
        VBox produitBox = new VBox(10);
        produitBox.setAlignment(Pos.CENTER);
        produitBox.setPadding(new Insets(10));
        produitBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ff8fb3; -fx-border-width: 1px; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        produitBox.setPrefWidth(200);
        produitBox.setMaxWidth(200);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        Image productImage = null;
        String imageUrlFromDb = produit.getImageUrl();
        String finalImageUrl = null;

        if (imageUrlFromDb != null && !imageUrlFromDb.trim().isEmpty()) {
            try {
                if (imageUrlFromDb.startsWith("/")) {
                    URL resourceUrl = getClass().getResource(imageUrlFromDb);
                    if (resourceUrl != null) {
                        finalImageUrl = resourceUrl.toExternalForm();
                    }
                }

                if (finalImageUrl == null) {
                    if (imageUrlFromDb.matches("^[a-zA-Z]:\\\\.*")) { // Chemin Windows absolu
                        finalImageUrl = "file:/" + imageUrlFromDb.replace("\\", "/");
                    } else if (imageUrlFromDb.startsWith("http")) { // URL distante
                        finalImageUrl = imageUrlFromDb;
                    } else { // Chemin relatif au système de fichiers (pas resource)
                        finalImageUrl = "file://" + imageUrlFromDb;
                    }
                }

                if (finalImageUrl != null) {
                    productImage = new Image(finalImageUrl, true);
                    if (productImage.isError()) {
                        System.err.println("ERREUR (Affichage): Erreur de chargement d'image pour " + produit.getNom() + " (URL finale tentée: " + finalImageUrl + "): " + productImage.getException().getMessage());
                        productImage = null; // Mark as error, fallback to default
                    }
                }
            } catch (Exception e) {
                System.err.println("ERREUR (Affichage): Exception lors du traitement de l'URL d'image pour " + produit.getNom() + " (URL de la DB: " + imageUrlFromDb + "): " + e.getMessage());
                e.printStackTrace();
                productImage = null; // Mark as error, fallback to default
            }
        }

        // Si l'image n'a pas pu être chargée ou est null, utiliser l'image par défaut
        if (productImage == null || productImage.isError()) {
            try {
                URL defaultImageUrl = getClass().getResource(DEFAULT_PRODUCT_IMAGE_PATH);
                if (defaultImageUrl == null) {
                    throw new NullPointerException("L'image par défaut n'a pas été trouvée à : " + DEFAULT_PRODUCT_IMAGE_PATH);
                }
                productImage = new Image(defaultImageUrl.toExternalForm(), true);
                if (productImage.isError()) {
                    System.err.println("ERREUR GRAVE (Affichage): L'image par défaut elle-même n'a pas pu être chargée : " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + productImage.getException().getMessage());
                }
            } catch (NullPointerException npe) {
                System.err.println("ERREUR (Affichage): Le chemin de l'image par défaut est incorrect ou le fichier n'existe pas. Veuillez vérifier: " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + npe.getMessage());
                npe.printStackTrace();
            } catch (Exception e) {
                System.err.println("ERREUR (Affichage): Exception lors du chargement de l'image par défaut: " + e.getMessage());
                e.printStackTrace();
            }
        }
        imageView.setImage(productImage);
        produitBox.getChildren().add(imageView);

        Label nomLabel = new Label(produit.getNom());
        nomLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
        nomLabel.setWrapText(true);
        nomLabel.setStyle("-fx-text-fill: #333333;");

        Label categorieLabel = new Label("Catégorie: " + produit.getCategorie());
        categorieLabel.setFont(Font.font("Segoe UI", 12));
        categorieLabel.setStyle("-fx-text-fill: #555555;");

        Label prixLabel = new Label("Prix: " + String.format("%.2f TND", produit.getPrix()));
        prixLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
        prixLabel.setStyle("-fx-text-fill: #ff8fb3;");

        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.getChildren().addAll(nomLabel, categorieLabel, prixLabel);
        produitBox.getChildren().add(detailsBox);

        Button ajouterAuPanierButton = new Button("🛒 Ajouter au Panier");
        ajouterAuPanierButton.setStyle("-fx-background-color: #ff8fb3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 20; " +
                "-fx-padding: 8 15 8 15; -fx-font-size: 12px; -fx-font-family: 'Segoe UI Semibold';");
        ajouterAuPanierButton.setOnAction(event -> {
            boolean added = servicePanier.ajouterProduitAuPanier(produit);
            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", produit.getNom() + " a été ajouté au panier !");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de " + produit.getNom() + " au panier.");
            }
        });

        Button detailsButton = new Button("ℹ️ Détails");
        detailsButton.setStyle("-fx-background-color: #f06292; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 20; " +
                "-fx-padding: 8 15 8 15; -fx-font-size: 12px; -fx-font-family: 'Segoe UI Semibold';");
        detailsButton.setOnAction(event -> {
            try {
                FXMLLoader detailLoader = new FXMLLoader(getClass().getResource("/views/User/DetailsProduitUser.fxml"));
                Parent detailRoot = detailLoader.load();
                DetailsProduitUserController detailController = detailLoader.getController();
                detailController.setProduitById(produit.getId());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(detailRoot));
                stage.setTitle("Détails du Produit: " + produit.getNom());
                stage.show();
            } catch (IOException e) {
                System.err.println("ERREUR (Affichage): Erreur lors de l'ouverture des détails du produit: " + e.getMessage());
                e.printStackTrace();
            }
        });

        HBox containerBoutons = new HBox(10);
        containerBoutons.setAlignment(Pos.CENTER);
        containerBoutons.getChildren().addAll(ajouterAuPanierButton, detailsButton);
        produitBox.getChildren().add(containerBoutons);

        return produitBox;
    }

    // Méthode déclenchée par le bouton de recherche par nom
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            // Si le champ est vide, afficher tous les produits non filtrés (ou appliquer l'autre filtre si présent)
            // Pour l'instant, on affiche tous les produits si le champ de nom est vide.
            afficherProduits(tousLesProduits);
        } else {
            try {
                // Utiliser la méthode de recherche par nom (existante)
                List<ProduitDerive> produitsRecherches = produitService.rechercherProduitsParNom(searchTerm);
                afficherProduits(produitsRecherches);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la recherche des produits par nom : " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de recherche", "Une erreur est survenue lors de la recherche par nom.");
            }
        }
    }

    // Nouvelle méthode pour filtrer par catégorie
    @FXML
    private void handleFilterByCategory(ActionEvent event) {
        String categoryTerm = categoryFilterField.getText().trim();
        if (categoryTerm.isEmpty()) {
            // Si le champ est vide, afficher tous les produits non filtrés (ou appliquer l'autre filtre si présent)
            afficherProduits(tousLesProduits);
        } else {
            try {
                List<ProduitDerive> produitsFiltres = produitService.filtrerProduitsParCategorie(categoryTerm);
                afficherProduits(produitsFiltres);
            } catch (SQLException e) {
                System.err.println("Erreur lors du filtrage des produits par catégorie : " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de filtrage", "Une erreur est survenue lors du filtrage par catégorie.");
            }
        }
    }

    // Nouvelle méthode pour réinitialiser le filtre de catégorie
    @FXML
    private void handleResetCategoryFilter(ActionEvent event) {
        categoryFilterField.clear();
        searchField.clear(); // Optionnel : réinitialiser aussi le champ de recherche par nom
        afficherProduits(tousLesProduits); // Affiche tous les produits non filtrés
    }


    @FXML
    public void retourToSelection(ActionEvent event) {
        try {
            URL location = getClass().getResource("/views/SelectionInterface.fxml");
            Parent selectionRoot = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/SelectionInterface.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(selectionRoot);
            stage.setScene(scene);
            stage.setTitle("Choisissez votre interface");
            stage.show();
        } catch (IOException e) {
            System.err.println("ERREUR (Affichage): Erreur lors du retour à l'interface de sélection : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToPanier(ActionEvent event) {
        try {
            URL location = getClass().getResource("/views/products/Panier.fxml");
            Parent panierRoot = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/products/Panier.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(panierRoot);
            stage.setScene(scene);
            stage.setTitle("Mon Panier");
            stage.show();
        } catch (IOException e) {
            System.err.println("ERREUR (Affichage): Erreur lors de la navigation vers le panier : " + e.getMessage());
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