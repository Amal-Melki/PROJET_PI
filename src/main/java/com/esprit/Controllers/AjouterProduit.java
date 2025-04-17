package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ProduitDeriveDAO;
import com.esprit.utils.DataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.util.Date;

public class AjouterProduit {

    @FXML private TextField tfNom;
    @FXML private TextArea taDescription;
    @FXML private TextField tfPrix;
    @FXML private TextField tfQuantite;
    @FXML private ComboBox<String> cbCategorie;

    private ProduitDeriveDAO dao;

    public void AjoutProduit() {
        Connection conn = DataSource.getInstance().getConnection();
        dao = new ProduitDeriveDAO(conn);
    }

    public AjouterProduit(ProduitDeriveDAO dao) {
        this.dao = dao;
    }

    @FXML
    public void initialize() {
        cbCategorie.getItems().addAll("Accessoires", "Vêtements", "Technologie", "Souvenirs");
    }

    @FXML
    private void ajouterProduit(ActionEvent event) {
        try {
            String nom = tfNom.getText();
            String description = taDescription.getText();
            double prix = Double.parseDouble(tfPrix.getText());
            int quantite = Integer.parseInt(tfQuantite.getText());
            String categorie = cbCategorie.getValue();

            ProduitDerive produit = new ProduitDerive(0, nom, description, prix, quantite, categorie, new Date());
            dao.ajouterProduit(produit);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Produit ajouté avec succès !");
            alert.showAndWait();

            // Clear fields
            tfNom.clear();
            taDescription.clear();
            tfPrix.clear();
            tfQuantite.clear();
            cbCategorie.getSelectionModel().clearSelection();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir correctement tous les champs !");
            alert.showAndWait();
        }
    }
}
