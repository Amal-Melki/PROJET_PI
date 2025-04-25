package com.esprit.controllers;

import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
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

public class ModifierFournisseur implements Initializable {

    @FXML
    private TableView<Fournisseur> tableFournisseurs;

    @FXML
    private TableColumn<Fournisseur, String> colNom;

    @FXML
    private TableColumn<Fournisseur, String> colEmail;

    @FXML
    private TableColumn<Fournisseur, Void> colAction;

    private final ObservableList<Fournisseur> data = FXCollections.observableArrayList();

    @FXML
    private Button btnRetourAccueil;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        chargerDonnees();
        ajouterColonneAction();
    }

    private void chargerDonnees() {
        data.clear();
        ServiceFournisseur sf = new ServiceFournisseur();
        List<Fournisseur> liste = sf.recuperer();
        data.addAll(liste);
        tableFournisseurs.setItems(data);
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnEffacer = new Button("Effacer");
            private final HBox box = new HBox(10, btnModifier, btnEffacer);

            {
                box.setAlignment(Pos.CENTER);

                String commonStyle = "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;";

                btnModifier.setStyle("-fx-background-color: #ff8cb3;" + commonStyle);
                btnEffacer.setStyle("-fx-background-color: #e57373;" + commonStyle);

                btnModifier.setOnAction(event -> {
                    Fournisseur f = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFournisseurFormulaire.fxml"));
                        Parent formulaire = loader.load();

                        ModifierFournisseurFormulaire controller = loader.getController();
                        controller.initData(f);

                        // ✅ Remplacer la scène dans la même fenêtre
                        Stage stageActuel = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stageActuel.setScene(new Scene(formulaire));
                        stageActuel.setTitle("Modifier Fournisseur");
                        stageActuel.sizeToScene(); // Adapter la taille automatiquement

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire.");
                    }
                });

                btnEffacer.setOnAction(event -> {
                    Fournisseur f = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Voulez-vous vraiment supprimer ce fournisseur ?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            ServiceFournisseur sf = new ServiceFournisseur();
                            sf.supprimer(f);
                            data.remove(f);
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
            stage.setTitle("Accueil - Gestion des Ressources");
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
