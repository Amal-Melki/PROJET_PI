package com.esprit.controllers;

import com.esprit.models.ReservationEspace;
import com.esprit.services.ReservationEspaceService;
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
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MesReservationsController implements Initializable {

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
    private ComboBox<String> comboStatusFilter;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnGallery;

    private final ObservableList<ReservationEspace> allReservations = FXCollections.observableArrayList();
    private final ObservableList<ReservationEspace> filteredReservations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
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
            allReservations.setAll(new ReservationEspaceService().getAll());
            reservationsTable.setItems(allReservations);
        } catch (Exception e) {
            showErrorAlert("Erreur", "Impossible de charger les réservations");
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
        try {
            allReservations.setAll(new ReservationEspaceService().getAll());
            filterReservations();
            showAlert("Actualisation", "Les réservations ont été actualisées");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Échec de l'actualisation: " + e.getMessage());
        }
    }

    @FXML
    public void showGalleryView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Gallery.fxml"));
            Stage stage = (Stage) btnGallery.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur", "Impossible d'ouvrir la galerie: " + e.getMessage());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
