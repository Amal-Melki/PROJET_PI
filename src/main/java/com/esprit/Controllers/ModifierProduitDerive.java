package com.esprit.Controllers;


import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierProduitDerive implements Initializable {

    @FXML
    private TableView<ProduitDerive> tableProduits;
    @FXML
    private TableColumn<ProduitDerive, String> colNom;
    @FXML
    private TableColumn<ProduitDerive, String> colCategorie;
    @FXML
    private TableColumn<ProduitDerive, Double> colPrix;
    @FXML
    private TableColumn<ProduitDerive, Integer> colStock;
    @FXML
    private TableColumn<ProduitDerive, String> colDescription;
    @FXML
    private TableColumn<ProduitDerive, Void> colAction;
    @FXML
    private TableColumn<ProduitDerive, String> colImageUrl;

    private final ObservableList<ProduitDerive> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImageUrl.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));


        ServiceProduitDerive spd = new ServiceProduitDerive();
        List<ProduitDerive> liste = spd.recuperer();
        data.addAll(liste);
        tableProduits.setItems(data);


        ajouterColonneModifier();
    }

    private void ajouterColonneModifier() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setStyle(
                        "-fx-background-color: #ff8cb3;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 15;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;"
                );

                btn.setOnAction(event -> {
                    ProduitDerive p = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduitDeriveFormulaire.fxml"));
                        Parent root = loader.load();


                        ModifierProduitDeriveFormulaire controller = loader.getController();
                        controller.initData(p);


                        Stage stage = new Stage();
                        stage.setTitle("Modifier Produit");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }
}