package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminProduitDerive {

    @FXML private TableView<ProduitDerive> tableProduits;
    @FXML private TableColumn<ProduitDerive, String> colNom;
    @FXML private TableColumn<ProduitDerive, String> colCategorie;
    @FXML private TableColumn<ProduitDerive, Double> colPrix;
    @FXML private TableColumn<ProduitDerive, Integer> colStock;
    @FXML private TableColumn<ProduitDerive, String> colDescription;
    @FXML private TableColumn<ProduitDerive, String> colImageUrl;
    @FXML private TableColumn<ProduitDerive, Void> colAction;

    private final ObservableList<ProduitDerive> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImageUrl.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        chargerDonnees();
        ajouterColonneAction();
    }

    private void chargerDonnees() {
        ServiceProduitDerive spd = new ServiceProduitDerive();
        List<ProduitDerive> produits = spd.recuperer();
        data.setAll(produits);
        tableProduits.setItems(data);
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox hBox = new HBox(10, btnModifier, btnSupprimer);

            {
                hBox.setAlignment(Pos.CENTER);
                btnModifier.setStyle("-fx-background-color: #ffa726; -fx-text-fill: white; -fx-background-radius: 10;");
                btnSupprimer.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-background-radius: 10;");

                btnModifier.setOnAction(e -> {
                    ProduitDerive produit = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/ModifierProduitDerive.fxml"));
                        Parent root = loader.load();

                        // Previous code did not pass the product to the controller
                        // So we just load and show the modification form

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Produit Dérivé");
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                btnSupprimer.setOnAction(e -> {
                    ProduitDerive produit = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce produit ?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            new ServiceProduitDerive().supprimer(produit);
                            data.remove(produit);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        });
    }

    @FXML
    void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Accueil.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
