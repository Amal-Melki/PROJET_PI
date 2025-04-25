package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierReservation implements Initializable {

    @FXML
    private TableView<ReservationMateriel> tableReservations;

    @FXML
    private TableColumn<ReservationMateriel, String> colNomMateriel;
    @FXML
    private TableColumn<ReservationMateriel, String> colDateDebut;
    @FXML
    private TableColumn<ReservationMateriel, String> colDateFin;
    @FXML
    private TableColumn<ReservationMateriel, Integer> colQuantite;
    @FXML
    private TableColumn<ReservationMateriel, String> colStatut;
    @FXML
    private TableColumn<ReservationMateriel, Void> colAction;

    private final ObservableList<ReservationMateriel> data = FXCollections.observableArrayList();
    @FXML
    private Button btnRetourAccueil;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNomMateriel.setCellValueFactory(new PropertyValueFactory<>("nomMateriel"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteReservee"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        ServiceReservationMateriel srm = new ServiceReservationMateriel();
        List<ReservationMateriel> reservations = srm.recuperer();

        ServiceMateriel sm = new ServiceMateriel();
        HashMap<Integer, String> mapMateriel = new HashMap<>();
        for (Materiels m : sm.recuperer()) {
            mapMateriel.put(m.getId(), m.getNom());
        }

        for (ReservationMateriel r : reservations) {
            r.setNomMateriel(mapMateriel.getOrDefault(r.getMaterielId(), "Inconnu"));
        }

        data.addAll(reservations);
        tableReservations.setItems(data);

        ajouterColonneAction();
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnEffacer = new Button("Effacer");
            private final HBox box = new HBox(10, btnModifier, btnEffacer);

            {
                // Centrage du HBox
                box.setAlignment(Pos.CENTER);

                // Style partagé
                String commonStyle = "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;";

                btnModifier.setStyle("-fx-background-color: #ff8cb3;" + commonStyle);
                btnEffacer.setStyle("-fx-background-color: #e57373;" + commonStyle);

                btnModifier.setOnAction(event -> {
                    ReservationMateriel reservation = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReservationFormulaire.fxml"));
                        Parent root = loader.load();
                        ModifierReservationFormulaire controller = loader.getController();
                        controller.setReservation(reservation);

                        // ✅ Remplacer la scène dans la même fenêtre
                        Stage stageActuel = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stageActuel.setScene(new Scene(root));
                        stageActuel.setTitle("Modifier Réservation");
                        stageActuel.sizeToScene();

                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire.");
                    }
                });

                btnEffacer.setOnAction(event -> {
                    ReservationMateriel r = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Voulez-vous vraiment supprimer cette réservation ?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            ServiceReservationMateriel service = new ServiceReservationMateriel();
                            service.supprimer2(r.getId());
                            data.remove(r); // Supprimer de l'affichage
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
