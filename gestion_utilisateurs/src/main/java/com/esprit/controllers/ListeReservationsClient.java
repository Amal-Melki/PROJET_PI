package com.esprit.controllers;

import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationsClient;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListeReservationsClient implements Initializable {

    @FXML private TableView<ReservationMateriel> tableReservations;
    @FXML private TableColumn<ReservationMateriel, String> colNomMateriel;
    @FXML private TableColumn<ReservationMateriel, String> colDateDebut;
    @FXML private TableColumn<ReservationMateriel, String> colDateFin;
    @FXML private TableColumn<ReservationMateriel, Integer> colQuantite;
    @FXML private TableColumn<ReservationMateriel, String> colStatut;
    @FXML private TableColumn<ReservationMateriel, Void> colActions;

    @FXML private TextField tfRecherche;
    @FXML private ComboBox<String> cbStatut;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;
    @FXML private Button btnRetourAccueil;

    private ObservableList<ReservationMateriel> data;
    private final ServiceReservationsClient service = new ServiceReservationsClient();
    private final int clientId = 6;
    @FXML private TextField tfRechercheReservation;
    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private DatePicker dpFiltreDebut;
    @FXML private DatePicker dpFiltreFin;
    @FXML
    private ImageView logoImage;
    private ObservableList<ReservationMateriel> toutesLesReservations = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNomMateriel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomMateriel()));
        colDateDebut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateDebut().toLocalDate().toString()));
        colDateFin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateFin().toLocalDate().toString()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantiteReservee()).asObject());
        colStatut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatut()));

        cbStatut.setItems(FXCollections.observableArrayList("EN_ATTENTE", "VALIDEE", "ANNULEE"));

        loadReservations();

        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> filtrer());
        cbStatut.valueProperty().addListener((obs, oldVal, newVal) -> filtrer());
        dpDebut.valueProperty().addListener((obs, oldVal, newVal) -> filtrer());
        dpFin.valueProperty().addListener((obs, oldVal, newVal) -> filtrer());

        addActionButtons();
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
        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }

    private void loadReservations() {
        List<ReservationMateriel> list = service.recupererParClient(clientId);
        ServiceMateriel sm = new ServiceMateriel();
        list.forEach(r -> r.setNomMateriel(sm.getNomById(r.getMaterielId())));
        data = FXCollections.observableArrayList(list);
        tableReservations.setItems(data);
    }

    private void filtrer() {
        String nom = tfRecherche.getText().toLowerCase();
        String statut = cbStatut.getValue();
        LocalDate debut = dpDebut.getValue();
        LocalDate fin = dpFin.getValue();

        ObservableList<ReservationMateriel> filtres = FXCollections.observableArrayList(data.stream()
                .filter(r -> r.getNomMateriel().toLowerCase().contains(nom))
                .filter(r -> statut == null || statut.isEmpty() || r.getStatut().equalsIgnoreCase(statut))
                .filter(r -> debut == null || !r.getDateDebut().toLocalDate().isBefore(debut))
                .filter(r -> fin == null || !r.getDateFin().toLocalDate().isAfter(fin))
                .collect(Collectors.toList()));

        tableReservations.setItems(filtres);
    }

    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnAnnuler = new Button("Annuler");
            private final HBox pane = new HBox(10, btnModifier, btnAnnuler);

            {
                pane.setStyle("-fx-alignment: center");
                btnModifier.setStyle("-fx-background-color: #ff8cb3; -fx-text-fill: white; -fx-background-radius: 15;");
                btnAnnuler.setStyle("-fx-background-color: #e57373; -fx-text-fill: white; -fx-background-radius: 15;");

                btnModifier.setOnAction(event -> {
                    ReservationMateriel r = getTableView().getItems().get(getIndex());

                    // 🚫 Blocage si réservation annulée
                    if (r.getStatut().equals("ANNULEE")) {
                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Réservation annulée",
                                "Cette réservation a été annulée par l'administration.\n\n" +
                                        "Vous ne pouvez plus la modifier. Pour toute question, contactez :\nevencia.esprit@gmail.com"
                        );
                        return;
                    }

                    // 🚫 Blocage si réservation validée
                    if (r.getStatut().equals("VALIDEE")) {
                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Modification non autorisée",
                                "Cette réservation a déjà été validée par l'administration.\n\n" +
                                        "Pour toute modification ou annulation, veuillez contacter :\nevencia.esprit@gmail.com"
                        );
                        return;
                    }

                    // 🚫 Blocage si déjà terminée
                    if (r.getDateFin().toLocalDate().isBefore(LocalDate.now())) {
                        showAlert(Alert.AlertType.WARNING, "Réservation terminée",
                                "Cette réservation est déjà terminée. Vous ne pouvez plus la modifier ou l'annuler.");
                        return;
                    }

                    // 🚫 Blocage si stock insuffisant
                    ServiceMateriel sm = new ServiceMateriel();
                    int stock = sm.getQuantiteById(r.getMaterielId());
                    if (r.getQuantiteReservee() > stock) {
                        showAlert(
                                Alert.AlertType.ERROR,
                                "Stock insuffisant",
                                "La quantité demandée dépasse le stock disponible.\n\n" +
                                        "Pour toute demande spécifique, veuillez contacter :\nevencia.esprit@gmail.com"
                        );
                        return;
                    }

                    // ✅ Si tout est OK
                    ouvrirFormulaireModification(r);
                });

                btnAnnuler.setOnAction(event -> {
                    ReservationMateriel r = getTableView().getItems().get(getIndex());

                    if (r.getStatut().equals("ANNULEE")) {
                        showAlert(Alert.AlertType.INFORMATION, "Déjà annulée",
                                "Cette réservation a déjà été annulée par l'administration.\n\nAucune autre action n'est possible.");
                        return;
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Demande d'annulation",
                            "Cette réservation ne peut pas être annulée via l'application.\n\n" +
                                    "Veuillez envoyer une demande d'annulation à l'adresse suivante :\nevencia.esprit@gmail.com");
                });



            }
            private void ouvrirFormulaireModification(ReservationMateriel reservation) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReservationClient.fxml"));
                    Parent root = loader.load();
                    ModifierReservationClient controller = loader.getController();
                    controller.setReservation(reservation);


                    Stage stageActuel = (Stage) tableReservations.getScene().getWindow();
                    stageActuel.setScene(new Scene(root));
                    stageActuel.setTitle("Modifier Réservation");
                    stageActuel.sizeToScene();

                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire.");
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    @FXML
    void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filtrerReservationsAvance() {
        String nomRecherche = tfRechercheReservation.getText() != null ? tfRechercheReservation.getText().toLowerCase() : "";
        String statutFiltre = cbFiltreStatut.getValue();
        LocalDate dateDebut = dpFiltreDebut.getValue();
        LocalDate dateFin = dpFiltreFin.getValue();

        ObservableList<ReservationMateriel> filtres = FXCollections.observableArrayList();

        for (ReservationMateriel r : toutesLesReservations) {
            boolean matchNom = r.getNomMateriel() != null && r.getNomMateriel().toLowerCase().contains(nomRecherche);
            boolean matchStatut = (statutFiltre == null || statutFiltre.isEmpty() || r.getStatut().equalsIgnoreCase(statutFiltre));
            boolean matchDate = true;

            if (dateDebut != null && r.getDateDebut().toLocalDate().isBefore(dateDebut)) matchDate = false;
            if (dateFin != null && r.getDateFin().toLocalDate().isAfter(dateFin)) matchDate = false;

            if (matchNom && matchStatut && matchDate) filtres.add(r);
        }

        // Remplacer table si besoin
        tableReservations.setItems(filtres); // ou tableEnAttente.setItems(...) selon ton UI
    }

}
