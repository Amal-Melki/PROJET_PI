package com.esprit.core.controllers.controllers;

import com.esprit.models.ReservationEspace;
import com.esprit.services.ReservationEspaceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GestionReservationsController implements Initializable {
    private final ReservationEspaceService reservationService = ReservationEspaceService.getInstance();
    private static final Logger logger = Logger.getLogger(GestionReservationsController.class.getName());

    @FXML private TableView<ReservationEspace> reservationsTable;
    @FXML private TableColumn<ReservationEspace, Integer> idColumn;
    @FXML private TableColumn<ReservationEspace, String> clientColumn;
    @FXML private TableColumn<ReservationEspace, LocalDate> debutColumn;
    @FXML private TableColumn<ReservationEspace, LocalDate> finColumn;
    @FXML private TableColumn<ReservationEspace, String> statusColumn;
    @FXML private TableColumn<ReservationEspace, Void> actionsColumn;

    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        setupFilters();
        loadReservations();
        setupTableRowFactory();
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        debutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        finColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        idColumn.setPrefWidth(60);
        clientColumn.setPrefWidth(150);
        debutColumn.setPrefWidth(120);
        finColumn.setPrefWidth(120);
        statusColumn.setPrefWidth(100);

        actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(param -> new TableCell<ReservationEspace, Void>() {
            private final Button confirmBtn = new Button("Confirmer");
            private final Button cancelBtn = new Button("Annuler");
            private final HBox container = new HBox(5, confirmBtn, cancelBtn);

            {
                confirmBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 10px;");
                cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");

                confirmBtn.setOnAction(e -> {
                    ReservationEspace reservation = getTableView().getItems().get(getIndex());
                    confirmReservation(reservation);
                });

                cancelBtn.setOnAction(e -> {
                    ReservationEspace reservation = getTableView().getItems().get(getIndex());
                    handleCancelReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    ReservationEspace reservation = getTableView().getItems().get(getIndex());
                    updateButtonStates(reservation);
                    setGraphic(container);
                }
            }

            private void updateButtonStates(ReservationEspace reservation) {
                String status = reservation.getStatus();
                confirmBtn.setDisable("Confirmée".equals(status) || "Annulée".equals(status) || "Terminée".equals(status));
                cancelBtn.setDisable("Annulée".equals(status) || "Terminée".equals(status));
            }
        });

        reservationsTable.getColumns().add(actionsColumn);
    }

    private void setupTableRowFactory() {
        reservationsTable.setRowFactory(tv -> {
            TableRow<ReservationEspace> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldReservation, newReservation) -> {
                if (newReservation != null) {
                    switch (newReservation.getStatus()) {
                        case "Confirmée": row.setStyle("-fx-background-color: #d5f4e6;"); break;
                        case "Annulée": row.setStyle("-fx-background-color: #fadbd8;"); break;
                        case "Terminée": row.setStyle("-fx-background-color: #ebf3fd;"); break;
                        case "En attente": row.setStyle("-fx-background-color: #fff3cd;"); break;
                        default: row.setStyle("");
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }

    private void setupFilters() {
        // Initialiser les filtres
        statusFilter.getItems().addAll("Tous", "Confirmée", "En attente", "Annulée", "Terminée");
        statusFilter.getSelectionModel().selectFirst();
        
        // Appliquer les filtres lors des changements
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        // Formater les dates
        dateDebutPicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            @Override 
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() 
                    ? LocalDate.parse(string, dateFormatter) 
                    : null;
            }
        });
        
        dateFinPicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            @Override 
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() 
                    ? LocalDate.parse(string, dateFormatter) 
                    : null;
            }
        });
    }
    
    private void applyFilters() {
        try {
            String status = statusFilter.getValue();
            LocalDate startDate = dateDebutPicker.getValue();
            LocalDate endDate = dateFinPicker.getValue();
            
            Predicate<ReservationEspace> predicate = r -> 
                (status == null || status.equals("Tous") || r.getStatus().equals(status))
                && (startDate == null || !r.getDateFin().isBefore(startDate))
                && (endDate == null || !r.getDateDebut().isAfter(endDate));
                
            reservationsTable.setItems(FXCollections.observableArrayList(
                reservationsTable.getItems().stream()
                    .filter(predicate)
                    .collect(Collectors.toList())
            ));
                
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du filtrage", e);
        }
    }

    @FXML
    private void handleReset() {
        try {
            statusFilter.getSelectionModel().clearSelection();
            dateDebutPicker.setValue(null);
            dateFinPicker.setValue(null);
            loadReservations();
            showAlert("Réinitialisation", "Les filtres ont été réinitialisés");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Échec de la réinitialisation: " + e.getMessage());
        }
    }

    private void showErrorAlert(String erreur, String s) {
    }

    @FXML
    private void handleNewReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NouvelleReservation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nouvelle Réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(reservationsTable.getScene().getWindow());

            stage.showAndWait();
            refreshTable();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre",
                    "Erreur lors de l'ouverture de la fenêtre de création : " + e.getMessage());
        }
    }

    @FXML
    private void showStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatistiquesReservations.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Statistiques des Réservations");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(reservationsTable.getScene().getWindow());

            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les statistiques",
                    "Erreur lors de l'ouverture de la fenêtre des statistiques : " + e.getMessage());
        }
    }

    @FXML
    private void handleExport() {
        showAlert(Alert.AlertType.INFORMATION, "Export", "Fonctionnalité d'export",
                "La fonctionnalité d'export sera implémentée ici.");
    }

    @FXML
    public void refreshTable() {
        loadReservations();
    }

    private void loadReservations() {
        try {
            List<ReservationEspace> reservations = ReservationEspaceService.getInstance().getAllReservations();
            reservationsTable.getItems().setAll(reservations);
            updateStatusLabel(reservations.size(), reservations.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement des réservations", e);
            showAlert("Erreur", "Échec du chargement", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    private void updateStatusLabel(int filteredCount, int totalCount) {
        System.out.println("Affichage de " + filteredCount + " réservations sur " + totalCount);
    }

    private void confirmReservation(ReservationEspace reservation) {
        if (reservation == null) return;

        if ("Confirmée".equals(reservation.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Réservation déjà confirmée",
                    "Cette réservation est déjà confirmée.");
            return;
        }

        if ("Annulée".equals(reservation.getStatus()) || "Terminée".equals(reservation.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Action impossible", "Réservation non modifiable",
                    "Cette réservation ne peut pas être confirmée.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer la réservation");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir confirmer cette réservation ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (ReservationEspaceService.getInstance().confirmReservation(reservation.getReservationId())) {
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation confirmée",
                                "La réservation a été confirmée avec succès.");
                        refreshTable();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de confirmation",
                                "La confirmation de la réservation a échoué.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la confirmation", e.getMessage());
                }
            }
        });
    }

    private void handleCancelReservation(ReservationEspace reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer l'annulation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Annuler la réservation #" + reservation.getReservationId() + " ?\nCette action est irréversible.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ReservationEspaceService.getInstance().cancelReservation(reservation.getReservationId());
                    logger.log(Level.INFO, "Réservation annulée : {0}", reservation.getReservationId());
                    loadReservations();
                    showAlert("Succès", "Réservation annulée", "La réservation a été annulée avec succès.");
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Erreur lors de l'annulation", e);
                    showAlert("Erreur", "Échec de l'annulation", "Impossible d'annuler la réservation : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, header, message);
    }

    private void showAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, null, message);
    }

    public ReservationEspace getSelectedReservation() {
        return reservationsTable.getSelectionModel().getSelectedItem();
    }

    public void selectReservation(ReservationEspace reservation) {
        reservationsTable.getSelectionModel().select(reservation);
    }
}