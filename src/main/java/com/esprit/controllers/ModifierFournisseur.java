package com.esprit.controllers;

import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Charger les fournisseurs
        ServiceFournisseur sf = new ServiceFournisseur();
        List<Fournisseur> liste = sf.recuperer();
        data.addAll(liste);
        tableFournisseurs.setItems(data);

        ajouterColonneAction();
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnEffacer = new Button("Effacer");
            private final HBox box = new HBox(10, btnModifier, btnEffacer);

            {
                box.setAlignment(Pos.CENTER); // Centrage des boutons

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
                        Parent root = loader.load();
                        ModifierFournisseurFormulaire controller = loader.getController();
                        controller.initData(f);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Fournisseur");
                        stage.setScene(new Scene(root));
                        stage.show();
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
                            sf.supprimer(f); // méthode qui supprime de la base
                            data.remove(f);  // mise à jour de l'affichage
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
}
