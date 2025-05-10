package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AjoutProduitDerive implements Initializable {

    @FXML
    private TextField tfNom;
    @FXML
    private ComboBox<String> cbCategorie;
    @FXML
    private TextField tfPrix;
    @FXML
    private TextField tfStock;
    @FXML
    private TextArea taDescription;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnSelectImage;
    @FXML
    private ImageView imagePreview;

    private String imagePath; // Pour stocker le chemin de l'image

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbCategorie.getItems().addAll("Vêtements", "Accessoires", "Goodies", "Autres");

        // Configuration initiale de l'ImageView
        imagePreview.setFitWidth(150);
        imagePreview.setFitHeight(150);
        imagePreview.setPreserveRatio(true);
    }

    @FXML
    void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        // Filtrer pour n'afficher que les fichiers images
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        // Ouvrir le dialogue de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Stocker le chemin absolu de l'image
            imagePath = selectedFile.getAbsolutePath();

            // Afficher l'image dans l'ImageView
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    @FXML
    void addProduit(ActionEvent event) {
        try {
            // Validation des champs obligatoires
            if (tfNom.getText().isEmpty() || cbCategorie.getValue() == null
                    || tfPrix.getText().isEmpty() || tfStock.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            if (imagePath == null || imagePath.isEmpty()) {
                showAlert("Erreur", "Veuillez sélectionner une image pour le produit.");
                return;
            }

            String nom = tfNom.getText();
            String categorie = cbCategorie.getValue();
            double prix = Double.parseDouble(tfPrix.getText());
            int stock = Integer.parseInt(tfStock.getText());
            String description = taDescription.getText();

            ProduitDerive produit = new ProduitDerive(nom, categorie, prix, stock, description, imagePath);
            ServiceProduitDerive spd = new ServiceProduitDerive();
            spd.ajouter(produit);

            showAlert("Succès", "Produit ajouté avec succès !", Alert.AlertType.CONFIRMATION);

            // Redirection vers la vue de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/produits/ModifierProduitDerive.fxml"));
            Parent root = loader.load();
            btnAjouter.getScene().setRoot(root);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix et stock doivent être des nombres valides.");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    @FXML
    void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/produits/AccueilProduitDerive.fxml"));
            Parent root = loader.load();
            btnAjouter.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à l'accueil.");
        }
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
