package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupprimerProduitDerive implements Initializable {

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
    private TableColumn<ProduitDerive, Void> colSupprimer;
    @FXML
    private Button btnRetour;

    private final ObservableList<ProduitDerive> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        chargerProduits();
        ajouterColonneSupprimer();
    }

    private void chargerProduits() {
        try {
            ServiceProduitDerive spd = new ServiceProduitDerive();
            List<ProduitDerive> liste = spd.recuperer();
            data.setAll(liste);
            tableProduits.setItems(data);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de chargement des produits : " + e.getMessage());
        }
    }

    private void ajouterColonneSupprimer() {
        colSupprimer.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setStyle(
                        "-fx-background-color: #ff4444;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 15;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;"
                );
                btn.setOnAction(event -> {
                    ProduitDerive produit = getTableView().getItems().get(getIndex());
                    confirmerSuppression(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(btn));
                }
            }
        });
    }

    private void confirmerSuppression(ProduitDerive produit) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le produit : " + produit.getNom());
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            supprimerProduit(produit);
        }
    }

    private void supprimerProduit(ProduitDerive produit) {
        try {
            ServiceProduitDerive service = new ServiceProduitDerive();
            service.supprimer(produit);
            data.remove(produit);
            showAlert("Succès", "Produit supprimé avec succès !", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de suppression : " + e.getMessage());
        }
    }

    @FXML
    void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/produits/AccueilProduitDerive.fxml"));
            Parent root = loader.load();
            btnRetour.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à l'accueil.");
        }
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
