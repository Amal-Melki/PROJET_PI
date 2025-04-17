package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ProduitDeriveDAO;
import com.esprit.utils.DataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.sql.Connection;
public class SupprimerProduit {
    @FXML private Label produitLabel;

    private ProduitDerive produit;
    private final ProduitDeriveDAO dao;

    public SupprimerProduit() {
        Connection conn = DataSource.getInstance().getConnection();
        dao = new ProduitDeriveDAO(conn);
    }

    public void initProduit(ProduitDerive produit) {
        this.produit = produit;
        produitLabel.setText("Voulez-vous vraiment supprimer : " + produit.getNom() + " ?");
    }

    @FXML
    public void supprimerProduit() {
        try {
            dao.deleteProduit(produit.getId());
            showAlert("Succès", "Produit supprimé avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la suppression.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
