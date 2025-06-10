package com.esprit.controllers;

import com.esprit.models.ReservationEspace;
import com.esprit.services.ReservationEspaceService;
import com.esprit.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MesReservationsController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MesReservationsController.class);

    @FXML
    private TableView<ReservationEspace> reservationsTable;

    // Colonnes TableView
    @FXML
    private TableColumn<ReservationEspace, Integer> colId;
    @FXML
    private TableColumn<ReservationEspace, String> colSpace;
    @FXML
    private TableColumn<ReservationEspace, LocalDate> colStartDate;
    @FXML
    private TableColumn<ReservationEspace, LocalDate> colEndDate;
    @FXML
    private TableColumn<ReservationEspace, Integer> colPeople;
    @FXML
    private TableColumn<ReservationEspace, String> colPhone;
    @FXML
    private TableColumn<ReservationEspace, String> colDescription;
    @FXML
    private TableColumn<ReservationEspace, String> colStatus;
    @FXML
    private TableColumn<ReservationEspace, String> colActions;

    @FXML
    private ComboBox<String> comboStatusFilter;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnGallery;

    private final ObservableList<ReservationEspace> allReservations = FXCollections.observableArrayList();
    private final ObservableList<ReservationEspace> filteredReservations = FXCollections.observableArrayList();

    private ReservationEspaceService reservationEspaceService;

    @FXML
    public void initialize() {
        setupTableColumns();
        reservationEspaceService = ReservationEspaceService.getInstance();
        loadData();
        setupFilter();
        setupButtons();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colSpace.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colPeople.setCellValueFactory(new PropertyValueFactory<>("nombrePersonnes"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("telephoneClient"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadData() {
        try {
            List<ReservationEspace> reservations = reservationEspaceService.getReservationsByUser(SessionManager.getCurrentUser().getId());
            
            allReservations.setAll(reservations);
            filteredReservations.setAll(reservations);
            reservationsTable.setItems(filteredReservations);
            
            if (reservations.isEmpty()) {
                showAlert("Information", "Aucune réservation trouvée");
            }
        } catch (Exception e) {
            logger.error("Erreur chargement réservations", e);
            showAlert("Erreur", "Échec du chargement des réservations", Alert.AlertType.ERROR);
        }
    }

    private void setupFilter() {
        comboStatusFilter.getItems().addAll("Tous", "En attente", "Confirmé", "Annulé");
        comboStatusFilter.setValue("Tous");
        comboStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterReservations());
    }

    private void setupButtons() {
        btnRefresh.setOnAction(e -> loadData());
        btnGallery.setOnAction(e -> showGalleryView());
    }

    private void filterReservations() {
        String selectedStatus = comboStatusFilter.getValue();

        if (selectedStatus == null || selectedStatus.equals("Tous")) {
            reservationsTable.setItems(allReservations);
        } else {
            filteredReservations.setAll(
                allReservations.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList())
            );
            reservationsTable.setItems(filteredReservations);
        }
    }

    @FXML
    public void refreshReservations() {
        loadData(); // Simplifié pour utiliser loadData()
    }

    @FXML
    private void showGalleryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/Gallery.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnGallery.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur", "Impossible d'ouvrir la galerie");
        }
    }

    private void cancelReservation(ReservationEspace reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation d'annulation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment annuler la réservation #" + reservation.getId() + " ?\nCette action est irréversible.");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (reservation.getStatus().equals("Annulée")) {
                    showAlert("Information", "Cette réservation est déjà annulée");
                    return;
                }
                
                boolean success = reservationEspaceService.cancelReservation(reservation.getId());
                if (success) {
                    logger.info("Réservation annulée : {}", reservation.getId());
                    loadData();
                    showAlert("Succès", "Réservation annulée avec succès");
                } else {
                    showAlert("Erreur", "Échec de l'annulation", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de l'annulation", e);
                showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }
}
