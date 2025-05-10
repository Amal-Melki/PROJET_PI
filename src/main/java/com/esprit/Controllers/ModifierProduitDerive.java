package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
    private TableColumn<ProduitDerive, String> colImageUrl;
    @FXML
    private TableColumn<ProduitDerive, Void> colAction;

    @FXML
    private Button btnRetourAccueil;

    private final ObservableList<ProduitDerive> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        data.clear();
        ServiceProduitDerive spd = new ServiceProduitDerive();
        List<ProduitDerive> liste = spd.recuperer();
        data.addAll(liste);
        tableProduits.setItems(data);
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnEffacer = new Button("Effacer");
            private final HBox box = new HBox(10, btnModifier, btnEffacer);

            {
                box.setAlignment(Pos.CENTER);

                String styleCommun = "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;";

                btnModifier.setStyle("-fx-background-color: #ff8cb3;" + styleCommun);
                btnEffacer.setStyle("-fx-background-color: #e57373;" + styleCommun);

                btnModifier.setOnAction(event -> {
                    ProduitDerive p = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/produits/ModifierProduitDeriveFormulaire.fxml"));
                        Parent formulaire = loader.load();

                        ModifierProduitDeriveFormulaire controller = loader.getController();
                        controller.initData(p);

                        Stage stageActuel = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stageActuel.setScene(new Scene(formulaire));
                        stageActuel.setTitle("Modifier Produit Dérivé");
                        stageActuel.sizeToScene();

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire.");
                    }
                });

                btnEffacer.setOnAction(event -> {
                    ProduitDerive p = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Voulez-vous vraiment supprimer ce produit dérivé ?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            ServiceProduitDerive spd = new ServiceProduitDerive();
                            spd.supprimer(p);
                            data.remove(p);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil - Gestion des Produits Dérivés");
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
