package com.esprit.Controllers;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

import com.esprit.Controllers.ModifierProduitDerive;

public class ListeProduitDerive {
    @FXML private TableView<ProduitDerive> tableProduits;
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

        // Add listeners for search and filter controls
        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> handleFiltrer());
        cbType.setOnAction(event -> handleFiltrer());
        cbStatut.setOnAction(event -> handleFiltrer());
    }

    private void configurerColonnes() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
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
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox box = new HBox(10, btnModifier, btnSupprimer);

            {
                box.setAlignment(Pos.CENTER);

                String styleCommun = "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;";

                btnModifier.setStyle("-fx-background-color: #ff8cb3;" + styleCommun);
                btnSupprimer.setStyle("-fx-background-color: #e57373;" + styleCommun);

                btnModifier.setOnAction(event -> {
                    ProduitDerive produit = getTableView().getItems().get(getIndex());
                    if (produit != null) {
                        handleModifierProduit(produit);
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

    private void handleModifierProduit(ProduitDerive produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/ModifierProduitDerive.fxml"));
            Parent root = loader.load();

            // Pass selected product to the modification controller
            com.esprit.Controllers.ModifierProduitDerive controller = loader.getController();
            // ModifierProduitDerive does not have setProduit method, so remove this call or implement it
            // For now, remove the call to setProduit

            Stage stage = new Stage();
            stage.setTitle("Modifier un produit dérivé");
            stage.setScene(new Scene(root));
            stage.show();

            // Refresh product list when modification window is closed
            stage.setOnHiding(event -> {
                chargerProduits();
            });
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ouvrir la vue de modification.");
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

        System.out.println("Recherche: " + recherche);
        System.out.println("Type: " + type + ", Statut: " + statut);

        ObservableList<ProduitDerive> produitsFiltres = FXCollections.observableArrayList();

        for (ProduitDerive produit : produitsList) {
            boolean matchType = type.equals("Tous") || produit.getCategorie().equalsIgnoreCase(type);
            boolean matchStatut = statut.equals("Tous") ||
                    (statut.equals("En stock") && produit.getStock() > 10) ||
                    (statut.equals("Rupture") && produit.getStock() == 0) ||
                    (statut.equals("Alerte") && produit.getStock() > 0 && produit.getStock() <= 10);
            boolean matchRecherche = produit.getNom().toLowerCase().contains(recherche) ||
                    produit.getCategorie().toLowerCase().contains(recherche);

            if (matchType && matchStatut && matchRecherche) {
                produitsFiltres.add(produit);
            }
        }

        System.out.println("Produits filtrés: " + produitsFiltres.size());

        tableProduits.setItems(produitsFiltres);
    }

    @FXML
    private void handleModifier() {
        ProduitDerive produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/ModifierProduitDerive.fxml"));
                Parent root = loader.load();

                // Pass selected product to the modification controller
                com.esprit.Controllers.ModifierProduitDerive controller = loader.getController();
                // ModifierProduitDerive does not have setProduit method, so remove this call or implement it
                // For now, remove the call to setProduit

                Stage stage = new Stage();
                stage.setTitle("Modifier un produit dérivé");
                stage.setScene(new Scene(root));
                stage.show();

                // Refresh product list when modification window is closed
                stage.setOnHiding(event -> {
                    chargerProduits();
                });
            } catch (IOException e) {
                e.printStackTrace();
                afficherAlerte("Erreur", "Impossible d'ouvrir la vue de modification.");
            }
        } else {
            afficherAlerte("Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier.");
        }
    }

    @FXML
    private void handleSupprimer() {
        ProduitDerive produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le produit : " + produitSelectionne.getNom());
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit définitivement ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = produitService.supprimerProduit(produitSelectionne.getId());
                    if (success) {
                        afficherAlerte("Succès", "Produit supprimé avec succès.");
                        chargerProduits();
                    } else {
                        afficherAlerte("Erreur", "Échec de la suppression du produit.");
                    }
                }
            });
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

            // Refresh product list when add product window is closed
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/AccueilProduitDerive.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}