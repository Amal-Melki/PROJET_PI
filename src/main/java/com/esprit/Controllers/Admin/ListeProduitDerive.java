// ListeProduitDerive.java
package com.esprit.controllers.Admin;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.Admin.ServiceProduitDerive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;

public class ListeProduitDerive {
    @FXML private TableView<ProduitDerive> tableProduits;
    @FXML private TableColumn<ProduitDerive, Integer> colId; // NOUVEAU: Colonne pour l'ID
    @FXML private TableColumn<ProduitDerive, String> colNom;
    @FXML private TableColumn<ProduitDerive, String> colCategorie;
    @FXML private TableColumn<ProduitDerive, Double> colPrix;
    @FXML private TableColumn<ProduitDerive, Integer> colStock;
    @FXML private TableColumn<ProduitDerive, String> colImageUrl;
    @FXML private TableColumn<ProduitDerive, Void> colAction;

    @FXML private Button btnRetour;
    @FXML private TextField tfRecherche;
    @FXML private ComboBox<String> cbType;
    @FXML private ComboBox<String> cbStatut;

    @FXML
    private Button btnRetourAccueil;

    private final ServiceProduitDerive produitService = new ServiceProduitDerive();
    private final ObservableList<ProduitDerive> produitsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerProduits();
        initialiserFiltres();
        ajouterColonneAction();

        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> handleFiltrer());
        cbType.setOnAction(event -> handleFiltrer());
        cbStatut.setOnAction(event -> handleFiltrer());

        tableProduits.setEditable(true);
    }

    private void configurerColonnes() {
        // NOUVEAU: Configuration de la colonne ID
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        // L'ID ne devrait pas être modifiable directement
        // colId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        // colId.setOnEditCommit(event -> { /* Ne rien faire ou gérer une erreur */ });

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setCellFactory(TextFieldTableCell.forTableColumn());
        colNom.setOnEditCommit(event -> {
            event.getRowValue().setNom(event.getNewValue());
        });

        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colCategorie.setCellFactory(TextFieldTableCell.forTableColumn());
        colCategorie.setOnEditCommit(event -> {
            event.getRowValue().setCategorie(event.getNewValue());
        });

        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colPrix.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPrix.setOnEditCommit(event -> {
            try {
                event.getRowValue().setPrix(event.getNewValue());
            } catch (NumberFormatException e) {
                afficherAlerte("Erreur", "Le prix doit être un nombre valide.");
                tableProduits.refresh();
            }
        });

        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colStock.setOnEditCommit(event -> {
            try {
                event.getRowValue().setStock(event.getNewValue());
            } catch (NumberFormatException e) {
                afficherAlerte("Erreur", "Le stock doit être un nombre entier valide.");
                tableProduits.refresh();
            }
        });

        colImageUrl.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));
    }

    private void chargerProduits() {
        produitsList.setAll(produitService.obtenirTousLesProduits());
        tableProduits.setItems(produitsList);
    }

    private void initialiserFiltres() {
        cbType.getItems().addAll("Tous", "T-shirt", "Mug", "Stickers", "Autre");
        cbType.setValue("Tous");

        cbStatut.getItems().addAll("Tous", "En stock", "Rupture", "Alerte");
        cbStatut.setValue("Tous");
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Enregistrer");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox box = new HBox(10, btnModifier, btnSupprimer);

            {
                box.setAlignment(Pos.CENTER);

                String styleCommun = "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;";

                btnModifier.setStyle("-fx-background-color: #4CAF50;" + styleCommun);
                btnSupprimer.setStyle("-fx-background-color: #e57373;" + styleCommun);

                btnModifier.setOnAction(event -> {
                    ProduitDerive produit = getTableView().getItems().get(getIndex());
                    if (produit != null) {
                        handleEnregistrerModification(produit);
                    }
                });

                btnSupprimer.setOnAction(event -> {
                    ProduitDerive produit = getTableView().getItems().get(getIndex());
                    if (produit != null) {
                        handleSupprimerProduit(produit);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void handleEnregistrerModification(ProduitDerive produit) {
        boolean success = produitService.modifierProd(produit);
        if (success) {
            afficherAlerte("Succès", "Produit mis à jour avec succès.");
            chargerProduits();
        } else {
            afficherAlerte("Erreur", "Échec de la mise à jour du produit.");
        }
    }

    private void handleSupprimerProduit(ProduitDerive produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le produit : " + produit.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit définitivement ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = produitService.supprimerProduit(produit.getId());
                if (success) {
                    afficherAlerte("Succès", "Produit supprimé avec succès.");
                    chargerProduits();
                } else {
                    afficherAlerte("Erreur", "Échec de la suppression du produit.");
                }
            }
        });
    }

    @FXML
    private void handleFiltrer() {
        String type = cbType.getValue();
        String statut = cbStatut.getValue();
        String recherche = tfRecherche.getText().toLowerCase();

        ObservableList<ProduitDerive> produitsFiltres = FXCollections.observableArrayList();

        for (ProduitDerive produit : produitsList) {
            boolean matchType = type.equals("Tous") || produit.getCategorie().equalsIgnoreCase(type);
            boolean matchStatut = statut.equals("Tous") ||
                    (statut.equals("En stock") && produit.getStock() > 10) ||
                    (statut.equals("Rupture") && produit.getStock() == 0) ||
                    (statut.equals("Alerte") && produit.getStock() > 0 && produit.getStock() <= 10);
            boolean matchRecherche = produit.getNom().toLowerCase().contains(recherche) ||
                    produit.getCategorie().toLowerCase().contains(recherche) || // Ajoutez la recherche par ID
                    String.valueOf(produit.getId()).contains(recherche); // Recherche par ID
            if (matchType && matchStatut && matchRecherche) {
                produitsFiltres.add(produit);
            }
        }

        tableProduits.setItems(produitsFiltres);
    }

    @FXML
    private void handleModifier() {
        // This method is no longer needed
    }

    @FXML
    private void handleSupprimer() {
        ProduitDerive produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            handleSupprimerProduit(produitSelectionne);
        } else {
            afficherAlerte("Aucun produit sélectionné", "Veuillez sélectionner un produit à supprimer.");
        }
    }

    @FXML
    private void handleVoirDetails() {
        ProduitDerive produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            // Afficher les détails du produit
        } else {
            afficherAlerte("Aucun produit sélectionné", "Veuillez sélectionner un produit à afficher.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void ajouterProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/AjoutProduitDerive.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit dérivé");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHiding(event -> {
                chargerProduits();
            });
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ouvrir la vue d'ajout.");
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/products/AccueilProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}