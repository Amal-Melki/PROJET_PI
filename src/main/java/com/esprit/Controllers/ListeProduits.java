package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ProduitDeriveDAO;
import com.esprit.utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class ListeProduits {
    @FXML private TableView<ProduitDerive> produitTable;
    @FXML private TableColumn<ProduitDerive, String> nomColumn;
    @FXML private TableColumn<ProduitDerive, String> categorieColumn;
    @FXML private TableColumn<ProduitDerive, Integer> quantiteColumn;

    private final ProduitDeriveDAO dao;
    private ObservableList<ProduitDerive> produitList;

    public ListeProduits() {
        Connection conn = DataSource.getInstance().getConnection();
        dao = new ProduitDeriveDAO(conn);
    }

    @FXML
    public void initialize() {
        nomColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        categorieColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategorie()));
        quantiteColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        loadProduits();
    }

    private void loadProduits() {
        List<ProduitDerive> produits = dao.getAllProduits();
        produitList = FXCollections.observableArrayList(produits);
        produitTable.setItems(produitList);
    }

    @FXML
    public void handleAjouter() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/AjoutProduit.fxml"));
        AnchorPane pane = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Ajouter un Produit");
        stage.setScene(new Scene(pane));
        stage.showAndWait();
        loadProduits(); // rafraîchir la liste après ajout
    }

    @FXML
    public void handleModifier() throws IOException {
        ProduitDerive selected = produitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun produit sélectionné !");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/ModifierProduitFormulaire.fxml"));
        AnchorPane pane = loader.load();

        ModifierProduit controller = loader.getController();
        controller.initProduit(selected);

        Stage stage = new Stage();
        stage.setTitle("Modifier Produit");
        stage.setScene(new Scene(pane));
        stage.showAndWait();
        loadProduits();
    }

    @FXML
    public void handleSupprimer() throws IOException {
        ProduitDerive selected = produitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun produit sélectionné !");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Voulez-vous vraiment supprimer ce produit ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dao.deleteProduit(selected.getId());
                loadProduits();
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
