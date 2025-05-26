package com.esprit.Controllers.User;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.User.ServiceProduitDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class DetailsProduitUserController {

    @FXML
    private ImageView productImageView;
    @FXML
    private Label productNameLabel;
    @FXML
    private Label productCategoryLabel;
    @FXML
    private Label productDescriptionLabel;
    @FXML
    private Label productPriceLabel;

    private ProduitDerive currentProduit;
    private ServiceProduitDetails serviceProduitDetails;

    // Chemin par défaut de l'image si celle de la DB échoue ou est manquante
    private static final String DEFAULT_PRODUCT_IMAGE_PATH = "/images/Mug4.jpg"; // Mis à jour pour utiliser Mug4.jpg

    @FXML
    public void initialize() {
        serviceProduitDetails = new ServiceProduitDetails();
    }

    public void setProduit(ProduitDerive produit) {
        this.currentProduit = produit;
        System.out.println("DEBUG (Details): setProduit appelé avec un objet ProduitDerive.");
        displayProduitDetails();
    }

    public void setProduitById(int productId) {
        System.out.println("DEBUG (Details): setProduitById appelé avec ID: " + productId);
        this.currentProduit = serviceProduitDetails.getProduitById(productId);
        if (this.currentProduit == null) {
            System.err.println("ERREUR (Details): Le produit avec l'ID " + productId + " n'a PAS été trouvé dans la base de données. Affichage des informations par défaut.");
        }
        displayProduitDetails();
    }

    private void displayProduitDetails() {
        if (currentProduit != null) {
            System.out.println("DEBUG (Details): Affichage des détails pour le produit: " + currentProduit.getNom() + " (ID: " + currentProduit.getId() + ")");
            productNameLabel.setText(currentProduit.getNom());
            productCategoryLabel.setText("Catégorie: " + currentProduit.getCategorie());
            productDescriptionLabel.setText("Description: " + (currentProduit.getDescription() != null ? currentProduit.getDescription() : "N/A"));
            productPriceLabel.setText(String.format("Prix: %.2f TND", currentProduit.getPrix()));

            Image productImage = null;
            String imageUrlFromDb = currentProduit.getImageUrl();
            String finalImageUrl = null; // Cette variable contiendra l'URL correctement formatée pour Image

            if (imageUrlFromDb != null && !imageUrlFromDb.trim().isEmpty()) {
                try {
                    // 1. Tenter de charger comme ressource interne (ex: /images/mon_image.png)
                    // Cela suppose que si l'URL dans la DB commence par '/', c'est une ressource interne
                    if (imageUrlFromDb.startsWith("/")) {
                        URL resourceUrl = getClass().getResource(imageUrlFromDb);
                        if (resourceUrl != null) {
                            finalImageUrl = resourceUrl.toExternalForm();
                            System.out.println("DEBUG (Details): Image détectée et chargée comme ressource classpath: " + finalImageUrl);
                        }
                    }

                    // Si non trouvée comme ressource, tenter de la traiter comme un chemin de fichier ou une URL web
                    if (finalImageUrl == null) {
                        // 2. Vérifier si c'est un chemin de fichier local (Windows ou Unix)
                        if (imageUrlFromDb.matches("^[a-zA-Z]:\\\\.*")) { // Chemin Windows (C:\...)
                            // Convertir le chemin Windows en URL "file:/" avec des slashes
                            finalImageUrl = "file:/" + imageUrlFromDb.replace("\\", "/");
                            System.out.println("DEBUG (Details): Image détectée comme chemin Windows, convertie en: " + finalImageUrl);
                        } else if (imageUrlFromDb.startsWith("/") && !imageUrlFromDb.startsWith("http")) { // Chemin Unix-like absolu (/home/...)
                            // Utiliser le protocole file:// pour les chemins Unix-like absolus
                            finalImageUrl = "file://" + imageUrlFromDb;
                            System.out.println("DEBUG (Details): Image détectée comme chemin Unix-like, convertie en: " + finalImageUrl);
                        } else {
                            // 3. Sinon, supposer que c'est une URL directe (web ou déjà correctement formatée)
                            finalImageUrl = imageUrlFromDb;
                            System.out.println("DEBUG (Details): Image traitée comme URL directe (web ou autre): " + finalImageUrl);
                        }
                    }

                    // Maintenant, tenter de charger l'image avec l'URL déterminée
                    if (finalImageUrl != null) {
                        productImage = new Image(finalImageUrl, true); // true pour chargement en arrière-plan
                        if (productImage.isError()) {
                            System.err.println("ERREUR (Details): Erreur de chargement d'image pour " + currentProduit.getNom() + " (URL finale tentée: " + finalImageUrl + "): " + productImage.getException().getMessage());
                            productImage = null; // Marquer comme échec pour le fallback
                        }
                    }
                } catch (Exception e) {
                    System.err.println("ERREUR (Details): Exception lors du traitement de l'URL d'image pour " + currentProduit.getNom() + " (URL de la DB: " + imageUrlFromDb + "): " + e.getMessage());
                    e.printStackTrace();
                    productImage = null; // Marquer comme échec pour le fallback
                }
            }

            // Fallback vers l'image par défaut si productImage est toujours null ou a des erreurs
            if (productImage == null || productImage.isError()) {
                try {
                    URL defaultImageUrl = getClass().getResource(DEFAULT_PRODUCT_IMAGE_PATH);
                    if (defaultImageUrl == null) {
                        // Si l'image par défaut elle-même n'est pas trouvée, c'est une erreur de configuration grave
                        throw new NullPointerException("L'image par défaut n'a pas été trouvée à : " + DEFAULT_PRODUCT_IMAGE_PATH);
                    }
                    productImage = new Image(defaultImageUrl.toExternalForm(), true);
                    if (productImage.isError()) {
                        System.err.println("ERREUR GRAVE (Details): L'image par défaut elle-même n'a pas pu être chargée : " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + productImage.getException().getMessage());
                    }
                } catch (NullPointerException npe) {
                    System.err.println("ERREUR (Details): Le chemin de l'image par défaut est incorrect ou le fichier n'existe pas. Veuillez vérifier: " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + npe.getMessage());
                    npe.printStackTrace();
                    productImage = null; // Ultime fallback : pas d'image
                } catch (Exception e) {
                    System.err.println("ERREUR (Details): Exception lors du chargement de l'image par défaut: " + e.getMessage());
                    e.printStackTrace();
                    productImage = null;
                }
            }
            productImageView.setImage(productImage);

        } else {
            // Gérer le cas où le produit n'est pas trouvé ou est null (aucun ID correspondant en DB)
            System.out.println("DEBUG (Details): Le produit est null, affichage des informations de 'Produit non trouvé'.");
            productNameLabel.setText("Produit non trouvé");
            productCategoryLabel.setText("");
            productDescriptionLabel.setText("Désolé, ce produit n'est pas disponible ou une erreur est survenue lors de sa récupération.");
            productPriceLabel.setText("");
            try {
                // Tenter de charger l'image par défaut même si le produit est null
                URL defaultImageUrl = Objects.requireNonNull(getClass().getResource(DEFAULT_PRODUCT_IMAGE_PATH));
                productImageView.setImage(new Image(defaultImageUrl.toExternalForm()));
            } catch (NullPointerException npe) {
                System.err.println("ERREUR (Details): L'image par défaut est introuvable même pour un produit non trouvé. Vérifiez: " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + npe.getMessage());
                productImageView.setImage(null);
            }
        }
    }

    @FXML
    private void acheterProduit(ActionEvent event) {
        if (currentProduit != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmer l'achat");
            alert.setHeaderText("Acheter " + currentProduit.getNom() + " ?");
            alert.setContentText("Le prix total est de " + String.format("%.2f TND", currentProduit.getPrix()) + ".");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (serviceProduitDetails.acheterProduit(currentProduit)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Produit acheté avec succès !");
                    successAlert.showAndWait();
                    retourAffichageProduits(event);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Impossible de procéder à l'achat pour le moment. (Vérifiez la logique d'achat)");
                    errorAlert.showAndWait();
                }
            }
        } else {
            System.err.println("ERREUR (Details): Tentative d'achat d'un produit null.");
            new Alert(Alert.AlertType.ERROR, "Aucun produit sélectionné pour l'achat.").showAndWait();
        }
    }

    @FXML
    private void supprimerDuPanier(ActionEvent event) {
        if (currentProduit != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmer la suppression");
            alert.setHeaderText("Supprimer " + currentProduit.getNom() + " du panier ?");
            alert.setContentText("Ceci retirera l'article de votre panier.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Assurez-vous que serviceProduitDetails.supprimerDuPanier est la bonne méthode.
                // Normalement, la suppression du panier serait gérée par un ServicePanier,
                // mais si ServiceProduitDetails l'implémente aussi, c'est bon.
                if (serviceProduitDetails.supprimerDuPanier(currentProduit)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Produit supprimé du panier avec succès !");
                    successAlert.showAndWait();
                    retourAffichageProduits(event);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Impossible de supprimer le produit du panier. (Vérifiez la logique de suppression)");
                    errorAlert.showAndWait();
                }
            }
        } else {
            System.err.println("ERREUR (Details): Tentative de suppression d'un produit null.");
            new Alert(Alert.AlertType.ERROR, "Aucun produit sélectionné pour la suppression.").showAndWait();
        }
    }

    @FXML
    private void retourAffichageProduits(ActionEvent event) {
        try {
            URL location = getClass().getResource("/views/User/AffichageProduitsUser.fxml");
            Parent root = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/User/AffichageProduitsUser.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Découvrez nos Produits");
            stage.show();
        } catch (IOException e) {
            System.err.println("ERREUR (Details): Erreur lors du retour à l'affichage des produits : " + e.getMessage());
            e.printStackTrace();
        }
    }
}