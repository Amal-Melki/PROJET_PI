package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AjoutProduitDerive {

    @FXML
    private TextField tfNom;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private TextField tfPrix;

    @FXML
    private TextField tfQuantite;

    @FXML
    private TextField tfSeuilAlerte;

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnRetour;

    @FXML
    private Label lblImagePath;

    private final ServiceProduitDerive produitService = new ServiceProduitDerive();

    private String imagePath = null;

    @FXML
    private void initialize() {
        cbType.getItems().addAll("T-shirt", "Mug", "Stickers", "Autre");
        cbType.setValue("T-shirt");
    }

    @FXML
    private void addProduit() {
        try {
            String nom = tfNom.getText();
            String categorie = cbType.getValue();
            double prix = Double.parseDouble(tfPrix.getText());
            int quantite = Integer.parseInt(tfQuantite.getText());
            int seuilAlerte = Integer.parseInt(tfSeuilAlerte.getText());
            String description = taDescription.getText();

            ProduitDerive nouveauProduit = new ProduitDerive();
            nouveauProduit.setNom(nom);
            nouveauProduit.setCategorie(categorie);
            nouveauProduit.setPrix(prix);
            nouveauProduit.setStock(quantite);
            // Assuming ProduitDerive has setSeuilAlerte and setDescription methods
            // Remove setSeuilAlerte call because it does not exist
            // nouveauProduit.setSeuilAlerte(seuilAlerte);
            nouveauProduit.setDescription(description);
            // Change setImagePath to setImageUrl
            nouveauProduit.setImageUrl(imagePath);

            boolean success = false;
            try {
                produitService.ajouter(nouveauProduit);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Produit ajouté avec succès.");
                alert.showAndWait();
                fermerFenetre();
            } else {
                afficherErreur("Erreur lors de l'ajout du produit.");
            }
        } catch (NumberFormatException e) {
            afficherErreur("Veuillez entrer des valeurs numériques valides pour le prix, la quantité et le seuil d'alerte.");
        }
    }

    @FXML
    private void choisirImage() {
        // Implement image chooser logic here
        // For now, just simulate image selection
        imagePath = "path/to/selected/image.png";
        lblImagePath.setText(imagePath);
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/AccueilProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            fermerFenetre();
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
