package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ProduitDeriveDAO;
import com.esprit.utils.DataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.util.Date;
public class ModifierProduit {

    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField quantiteField;
    @FXML private TextField categorieField;

    private ProduitDerive produit;
    private final ProduitDeriveDAO dao;

    public ModifierProduit() {
        Connection conn = DataSource.getInstance().getConnection();
        dao = new ProduitDeriveDAO(conn);
    }

    public void initProduit(ProduitDerive produit) {
        this.produit = produit;
        nomField.setText(produit.getNom());
        descriptionField.setText(produit.getDescription());
        prixField.setText(String.valueOf(produit.getPrix()));
        quantiteField.setText(String.valueOf(produit.getQuantite()));
        categorieField.setText(produit.getCategorie());
    }

    @FXML
    public void modifierProduit() {
        try {
            produit.setNom(nomField.getText());
            produit.setDescription(descriptionField.getText());
            produit.setPrix(Double.parseDouble(prixField.getText()));
            produit.setQuantite(Integer.parseInt(quantiteField.getText()));
            produit.setCategorie(categorieField.getText());
            produit.setDateAjout(new Date()); // ou garder l'ancienne date

            dao.updateProduit(produit);
            showAlert("Succès", "Produit modifié avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Données invalides.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
