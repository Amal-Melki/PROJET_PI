package com.esprit.Controllers;


import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    private TextField tfImageUrl;
    @FXML
    private Button btnAjouter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbCategorie.getItems().addAll("Vêtements", "Accessoires", "Goodies", "Autres");
    }

    @FXML
    void addProduit(ActionEvent event) throws IOException {
        try {
            String nom = tfNom.getText();
            String categorie = cbCategorie.getValue();
            double prix = Double.parseDouble(tfPrix.getText());
            int stock = Integer.parseInt(tfStock.getText());
            String description = taDescription.getText();
            String imageUrl = tfImageUrl.getText();

            ProduitDerive produit = new ProduitDerive(nom, categorie, prix, stock, description, imageUrl);
            ServiceProduitDerive spd = new ServiceProduitDerive();
            spd.ajouter(produit);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ajout réussi");
            alert.setContentText("Produit ajouté avec succès !");
            alert.show();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduitDerive.fxml"));
            Parent root = loader.load();
            btnAjouter.getScene().setRoot(root);

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Prix et stock doivent être des nombres valides.");
            alert.show();
        }
    }
}