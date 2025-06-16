package com.esprit.controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.Admin.ServiceProduitDerive;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ModifierProduitDerive {

    @FXML private ComboBox<String> cbType;
    @FXML private ComboBox<String> cbStatut;
    @FXML private TextField tfRecherche;

    @FXML private TableView<ProduitDerive> tableProduits;
    @FXML private TableColumn<ProduitDerive, String> colNom;
    @FXML private TableColumn<ProduitDerive, String> colCategorie;
    @FXML private TableColumn<ProduitDerive, Double> colPrix;
    @FXML private TableColumn<ProduitDerive, Integer> colStock;
    @FXML private TableColumn<ProduitDerive, String> colDescription;
    @FXML private TableColumn<ProduitDerive, String> colImageUrl;
    @FXML private TableColumn<ProduitDerive, Void> colAction;

    @FXML private Pane formPane;
    @FXML private TextField tfNomEdit;
    @FXML private ComboBox<String> cbCategorieEdit;
    @FXML private TextField tfPrixEdit;
    @FXML private TextField tfStockEdit;
    @FXML private TextArea taDescriptionEdit;
    @FXML private TextField tfImageUrlEdit;
    @FXML private Button btnSaveEdit;
    @FXML private Button btnCancelEdit;

    @FXML private Button btnRetourAccueil;

    private final ObservableList<ProduitDerive> data = FXCollections.observableArrayList();
    private ProduitDerive selectedProduit;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImageUrl.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        chargerDonnees();
        setupFilters();
        ajouterColonneAction();

        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    private void chargerDonnees() {
        ServiceProduitDerive spd = new ServiceProduitDerive();
        List<ProduitDerive> produits = spd.recuperer();
        data.setAll(produits);
        tableProduits.setItems(data);
    }

    private void setupFilters() {
        // Example categories and statuses, adjust as needed
        cbType.setItems(FXCollections.observableArrayList("Type1", "Type2", "Type3"));
        cbStatut.setItems(FXCollections.observableArrayList("Disponible", "Indisponible"));

        cbType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleFiltrer(null));
        cbStatut.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleFiltrer(null));
        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> handleFiltrer(null));
    }

    @FXML
    private void handleFiltrer(ActionEvent event) {
        String typeFilter = cbType.getValue();
        String statutFilter = cbStatut.getValue();
        String searchText = tfRecherche.getText() != null ? tfRecherche.getText().toLowerCase() : "";

        List<ProduitDerive> filtered = data.stream()
                .filter(p -> (typeFilter == null || typeFilter.isEmpty() || p.getCategorie().equals(typeFilter)))
                .filter(p -> (statutFilter == null || statutFilter.isEmpty() || (statutFilter.equals("Disponible") && p.getStock() > 0) || (statutFilter.equals("Indisponible") && p.getStock() <= 0)))
                .filter(p -> p.getNom().toLowerCase().contains(searchText) || p.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        tableProduits.setItems(FXCollections.observableArrayList(filtered));
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");

            {
                btnEdit.setStyle("-fx-background-color: #ffa726; -fx-text-fill: white; -fx-background-radius: 10;");
                btnEdit.setOnAction(e -> {
                    selectedProduit = getTableView().getItems().get(getIndex());
                    showEditForm(selectedProduit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEdit);
            }
        });
    }

    private void showEditForm(ProduitDerive produit) {
        if (produit == null) return;
        formPane.setVisible(true);
        formPane.setManaged(true);

        tfNomEdit.setText(produit.getNom());
        cbCategorieEdit.setValue(produit.getCategorie());
        tfPrixEdit.setText(String.valueOf(produit.getPrix()));
        tfStockEdit.setText(String.valueOf(produit.getStock()));
        taDescriptionEdit.setText(produit.getDescription());
        tfImageUrlEdit.setText(produit.getImageUrl());
    }

    @FXML
    private void saveEdit(ActionEvent event) {
        if (selectedProduit == null) return;

        selectedProduit.setNom(tfNomEdit.getText());
        selectedProduit.setCategorie(cbCategorieEdit.getValue());
        try {
            selectedProduit.setPrix(Double.parseDouble(tfPrixEdit.getText()));
            selectedProduit.setStock(Integer.parseInt(tfStockEdit.getText()));
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix et Stock doivent être des nombres valides.");
            return;
        }
        selectedProduit.setDescription(taDescriptionEdit.getText());
        selectedProduit.setImageUrl(tfImageUrlEdit.getText());

        new ServiceProduitDerive().modifier(selectedProduit);
        chargerDonnees();
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML
    private void cancelEdit(ActionEvent event) {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("views/Accueil.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
