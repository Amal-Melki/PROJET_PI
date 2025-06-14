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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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

    @FXML
    private TextField tfRechercheReservation;
    @FXML
    private ImageView logoImage;
    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private DatePicker dpFiltreDebut;
    @FXML private DatePicker dpFiltreFin;
    @FXML
    private TableColumn<ReservationMateriel, Integer> colClientId;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colNomMateriel.setCellValueFactory(new PropertyValueFactory<>("nomMateriel"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteReservee"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colClientId.setCellValueFactory(new PropertyValueFactory<>("idClient"));

        chargerDonnees();
        ajouterColonneAction();

        tfRechercheReservation.textProperty().addListener((obs, oldValue, newValue) -> {
            filtrerReservations(newValue);
        });

        cbFiltreStatut.setItems(FXCollections.observableArrayList("EN_ATTENTE", "VALIDEE", "ANNULEE"));

        tfRechercheReservation.textProperty().addListener((obs, oldVal, newVal) -> filtrerReservationsAvance());
        cbFiltreStatut.valueProperty().addListener((obs, oldVal, newVal) -> filtrerReservationsAvance());
        dpFiltreDebut.valueProperty().addListener((obs, oldVal, newVal) -> filtrerReservationsAvance());
        dpFiltreFin.valueProperty().addListener((obs, oldVal, newVal) -> filtrerReservationsAvance());
        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);

                if (empty || statut == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label label = new Label(statut);
                label.setStyle("-fx-padding: 4 10 4 10; -fx-background-radius: 10; -fx-text-fill: white;");

                switch (statut) {
                    case "VALIDEE":
                        label.setStyle(label.getStyle() + "-fx-background-color: #4CAF50;"); // Vert
                        break;
                    case "ANNULEE":
                        label.setStyle(label.getStyle() + "-fx-background-color: #F44336;"); // Rouge
                        break;
                    case "EN_ATTENTE":
                        label.setStyle(label.getStyle() + "-fx-background-color: #2196F3;"); // Bleu
                        break;
                    default:
                        label.setStyle(label.getStyle() + "-fx-background-color: gray;");
                }

                setGraphic(label);
                setText(null);
            }
        });

    }

    private void chargerDonnees() {
        data.clear();
        ServiceReservationMateriel srm = new ServiceReservationMateriel();
        List<ReservationMateriel> reservations = srm.rechercher();

        ServiceMateriel sm = new ServiceMateriel();
        HashMap<Integer, String> mapMateriel = new HashMap<>();
        for (Materiels m : sm.rechercher()) {
            mapMateriel.put(m.getId(), m.getNom());
        }

        for (ReservationMateriel r : reservations) {
            r.setNomMateriel(mapMateriel.getOrDefault(r.getMaterielId(), "Inconnu"));
        }

        data.addAll(reservations);
        tableReservations.setItems(data);
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
                    ReservationMateriel reservation = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReservationFormulaire.fxml"));
                        Parent root = loader.load();
                        ModifierReservationFormulaire controller = loader.getController();
                        controller.setReservation(reservation);

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

                            chargerDonnees();
                            tableReservations.refresh();
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

    private void filtrerReservations(String nomRecherche) {
        if (nomRecherche == null || nomRecherche.trim().isEmpty()) {
            tableReservations.setItems(data);
            return;
        }

        String recherche = nomRecherche.toLowerCase();
        ObservableList<ReservationMateriel> filtres = FXCollections.observableArrayList();

        for (ReservationMateriel r : data) {
            if (r.getNomMateriel() != null && r.getNomMateriel().toLowerCase().contains(recherche)) {
                filtres.add(r);
            }
        }

        tableReservations.setItems(filtres);
    }
    @FXML
    private void filtrerReservationsAvance() {
        String nomRecherche = tfRechercheReservation.getText() != null ? tfRechercheReservation.getText().toLowerCase() : "";
        String statutFiltre = cbFiltreStatut.getValue();
        LocalDate dateDebutFiltre = dpFiltreDebut.getValue();
        LocalDate dateFinFiltre = dpFiltreFin.getValue();

        ObservableList<ReservationMateriel> filtres = FXCollections.observableArrayList();

        for (ReservationMateriel r : data) {
            boolean matchNom = r.getNomMateriel() != null && r.getNomMateriel().toLowerCase().contains(nomRecherche);
            boolean matchStatut = (statutFiltre == null || statutFiltre.isEmpty() || r.getStatut().equalsIgnoreCase(statutFiltre));
            boolean matchDate = true;

            if (dateDebutFiltre != null && r.getDateDebut().toLocalDate().isBefore(dateDebutFiltre)) {
                matchDate = false;
            }

            if (dateFinFiltre != null && r.getDateFin().toLocalDate().isAfter(dateFinFiltre)) {
                matchDate = false;
            }

            if (matchNom && matchStatut && matchDate) {
                filtres.add(r);
            }
        }

        tableReservations.setItems(filtres);
    }
}
