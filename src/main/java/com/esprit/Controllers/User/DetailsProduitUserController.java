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

    // OLD: Label for Product ID - Supprimez ou commentez cette ligne
    // @FXML
    // private Label productIdLabel;

    private ProduitDerive currentProduit;
    private ServiceProduitDetails serviceProduitDetails;

    private static final String DEFAULT_PRODUCT_IMAGE_PATH = "/images/Mug4.jpg";

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
            // OLD: Set the Product ID Label - Supprimez ou commentez cette ligne
            // productIdLabel.setText("ID: " + currentProduit.getId());
            productCategoryLabel.setText("Catégorie: " + currentProduit.getCategorie());
            productDescriptionLabel.setText("Description: " + (currentProduit.getDescription() != null ? currentProduit.getDescription() : "N/A"));
            productPriceLabel.setText(String.format("Prix: %.2f TND", currentProduit.getPrix()));

            Image productImage = null;
            String imageUrlFromDb = currentProduit.getImageUrl();
            String finalImageUrl = null;

            if (imageUrlFromDb != null && !imageUrlFromDb.trim().isEmpty()) {
                try {
                    if (imageUrlFromDb.startsWith("/")) {
                        URL resourceUrl = getClass().getResource(imageUrlFromDb);
                        if (resourceUrl != null) {
                            finalImageUrl = resourceUrl.toExternalForm();
                            System.out.println("DEBUG (Details): Image détectée et chargée comme ressource classpath: " + finalImageUrl);
                        }
                    }

                    if (finalImageUrl == null) {
                        if (imageUrlFromDb.matches("^[a-zA-Z]:\\\\.*")) {
                            finalImageUrl = "file:/" + imageUrlFromDb.replace("\\", "/");
                            System.out.println("DEBUG (Details): Image détectée comme chemin Windows, convertie en: " + finalImageUrl);
                        } else if (imageUrlFromDb.startsWith("/") && !imageUrlFromDb.startsWith("http")) {
                            finalImageUrl = "file://" + imageUrlFromDb;
                            System.out.println("DEBUG (Details): Image détectée comme chemin Unix-like, convertie en: " + finalImageUrl);
                        } else {
                            finalImageUrl = imageUrlFromDb;
                            System.out.println("DEBUG (Details): Image traitée comme URL directe (web ou autre): " + finalImageUrl);
                        }
                    }

                    if (finalImageUrl != null) {
                        productImage = new Image(finalImageUrl, true);
                        if (productImage.isError()) {
                            System.err.println("ERREUR (Details): Erreur de chargement d'image pour " + currentProduit.getNom() + " (URL finale tentée: " + finalImageUrl + "): " + productImage.getException().getMessage());
                            productImage = null;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("ERREUR (Details): Exception lors du traitement de l'URL d'image pour " + currentProduit.getNom() + " (URL de la DB: " + imageUrlFromDb + "): " + e.getMessage());
                    e.printStackTrace();
                    productImage = null;
                }
            }

            if (productImage == null || productImage.isError()) {
                try {
                    URL defaultImageUrl = getClass().getResource(DEFAULT_PRODUCT_IMAGE_PATH);
                    if (defaultImageUrl == null) {
                        throw new NullPointerException("L'image par défaut n'a pas été trouvée à : " + DEFAULT_PRODUCT_IMAGE_PATH);
                    }
                    productImage = new Image(defaultImageUrl.toExternalForm(), true);
                    if (productImage.isError()) {
                        System.err.println("ERREUR GRAVE (Details): L'image par défaut elle-même n'a pas pu être chargée : " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + productImage.getException().getMessage());
                    }
                } catch (NullPointerException npe) {
                    System.err.println("ERREUR (Details): Le chemin de l'image par défaut est incorrect ou le fichier n'existe pas. Veuillez vérifier: " + DEFAULT_PRODUCT_IMAGE_PATH + " - " + npe.getMessage());
                    npe.printStackTrace();
                    productImage = null;
                } catch (Exception e) {
                    System.err.println("ERREUR (Details): Exception lors du chargement de l'image par défaut: " + e.getMessage());
                    e.printStackTrace();
                    productImage = null;
                }
            }
            productImageView.setImage(productImage);

        } else {
            System.out.println("DEBUG (Details): Le produit est null, affichage des informations de 'Produit non trouvé'.");
            productNameLabel.setText("Produit non trouvé");
            // OLD: Clear ID label if product is null - Supprimez ou commentez cette ligne
            // productIdLabel.setText("");
            productCategoryLabel.setText("");
            productDescriptionLabel.setText("Désolé, ce produit n'est pas disponible ou une erreur est survenue lors de sa récupération.");
            productPriceLabel.setText("");
            try {
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