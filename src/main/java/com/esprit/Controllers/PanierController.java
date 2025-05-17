package com.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.esprit.services.produits.ServicePanier;

public class PanierController {

    @FXML
    private VBox panierVBox;
    @FXML
    private ListView<String> listePanier;
    @FXML
    private Button btnValiderPanier;
    @FXML
    private Button btnViderPanier;
    @FXML
    private Button btnRetour;

    private ServicePanier panierService;

    public PanierController() {
        panierService = new ServicePanier();
    }

    @FXML
    private void initialize() {
        updatePanierList();

        btnViderPanier.setOnAction(this::viderPanier);
        btnValiderPanier.setOnAction(this::validerPanier);
        btnRetour.setOnAction(this::retourAccueil);
    }

    private void updatePanierList() {
        listePanier.getItems().clear();
        listePanier.getItems().addAll(panierService.getItemsPanier());
    }

    @FXML
    private void viderPanier(ActionEvent event) {
        panierService.viderPanier();
        updatePanierList();
    }

    @FXML
    private void validerPanier(ActionEvent event) {
        panierService.validerPanier();
        updatePanierList();
        System.out.println("Commande validée!");
    }

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/AccueilProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
