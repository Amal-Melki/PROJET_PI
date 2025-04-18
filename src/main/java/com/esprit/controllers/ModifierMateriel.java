package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
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
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        ServiceMateriel sm = new ServiceMateriel();
        List<Materiels> liste = sm.recuperer();
        data.addAll(liste);
        tableMateriels.setItems(data);

        ajouterColonneAction();
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
                    Materiels m = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMaterielFormulaire.fxml"));
                        Parent root = loader.load();

                        ModifierMaterielFormulaire controller = loader.getController();
                        controller.initData(m);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Matériel");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire.");
                    }
                });

                btnEffacer.setOnAction(event -> {
                    Materiels m = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Voulez-vous vraiment supprimer ce matériel ?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            ServiceMateriel service = new ServiceMateriel();
                            service.supprimer(m); // Suppression depuis la base
                            data.remove(m); // Suppression depuis la liste affichée
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
