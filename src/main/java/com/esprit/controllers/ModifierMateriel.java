package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
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

public class ModifierMateriel implements Initializable {

    @FXML
    private TableView<Materiels> tableMateriels;

    @FXML
    private TableColumn<Materiels, String> colNom;
    @FXML
    private TableColumn<Materiels, String> colType;
    @FXML
    private TableColumn<Materiels, Integer> colQuantite;
    @FXML
    private TableColumn<Materiels, String> colEtat;
    @FXML
    private TableColumn<Materiels, String> colDescription;
    @FXML
    private TableColumn<Materiels, Void> colAction;

    private final ObservableList<Materiels> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lier les colonnes aux attributs
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Charger depuis la BDD
        ServiceMateriel sm = new ServiceMateriel();
        List<Materiels> liste = sm.recuperer();
        data.addAll(liste);
        tableMateriels.setItems(data);

        // Ajouter colonne bouton
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
                    Materiels m = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMaterielFormulaire.fxml"));
                        Parent root = loader.load();

                        // Passer l'objet au contrôleur
                        ModifierMaterielFormulaire controller = loader.getController();
                        controller.initData(m);

                        // Ouvrir dans une nouvelle fenêtre
                        Stage stage = new Stage();
                        stage.setTitle("Modifier Matériel");
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
