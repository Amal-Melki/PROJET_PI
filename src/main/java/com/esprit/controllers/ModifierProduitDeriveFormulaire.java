package com.esprit.controllers;


import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.Admin.ServiceProduitDerive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierProduitDeriveFormulaire {

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
    private Button btnModifier;

    private ProduitDerive produit;

    @FXML
    public void initialize() {
        cbCategorie.getItems().addAll("Vêtements", "Accessoires", "Goodies", "Autres");
    }

    public void initData(ProduitDerive p) {
        this.produit = p;

        tfNom.setText(p.getNom());
        cbCategorie.setValue(p.getCategorie());
        tfPrix.setText(String.valueOf(p.getPrix()));
        tfStock.setText(String.valueOf(p.getStock()));
        taDescription.setText(p.getDescription());
        tfImageUrl.setText(p.getImageUrl());
    }

    @FXML
    void modifierProduit(ActionEvent event) {
        try {
            produit.setNom(tfNom.getText());
            produit.setCategorie(cbCategorie.getValue());
            produit.setPrix(Double.parseDouble(tfPrix.getText()));
            produit.setStock(Integer.parseInt(tfStock.getText()));
            produit.setDescription(taDescription.getText());
            produit.setImageUrl(tfImageUrl.getText());

            ServiceProduitDerive service = new ServiceProduitDerive();
            service.modifier(produit);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Produit modifié avec succès !");
            alert.showAndWait();


            Stage stage = (Stage) btnModifier.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Prix et stock doivent être des nombres valides !");
            alert.show();
        }
    }

    @FXML
    private void handleInsererImage(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        java.io.File selectedFile = fileChooser.showOpenDialog(btnModifier.getScene().getWindow());
        if (selectedFile != null) {
            tfImageUrl.setText(selectedFile.toURI().toString());
        }
    }
}
