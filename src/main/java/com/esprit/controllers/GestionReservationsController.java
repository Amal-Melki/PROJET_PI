package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GestionReservationsController implements Initializable {

    @FXML
    private Button btnAddReservation;

    @FXML
    private Button btnRefresh;

    @FXML
    private ComboBox<String> comboFilterStatus;

    @FXML
    private ComboBox<Espace> comboFilterEspace;

    @FXML
    private DatePicker dateFilterStart;

    @FXML
    private DatePicker dateFilterEnd;

    @FXML
    private Button btnFilter;

    @FXML
    private Button btnClearFilters;

    @FXML
    private TableView<ReservationEspace> tableReservations;

    @FXML
    private TableColumn<ReservationEspace, Integer> colId;

    @FXML
    private TableColumn<ReservationEspace, String> colEspace;

    @FXML
    private TableColumn<ReservationEspace, String> colClient;

    @FXML
    private TableColumn<ReservationEspace, String> colDateDebut;

    @FXML
    private TableColumn<ReservationEspace, String> colDateFin;

    @FXML
    private TableColumn<ReservationEspace, String> colStatut;

    @FXML
    private TableColumn<ReservationEspace, Double> colPrix;

    @FXML
    private TableColumn<ReservationEspace, Void> colActions;

    @FXML
    private Label lblTotalReservations;

    @FXML
    private Label lblActiveReservations;

    @FXML
    private Label lblUpcomingReservations;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Pagination paginationReservations;

    @FXML
    private BarChart<String, Number> reservationsByMonthChart;

    @FXML
    private PieChart reservationsByStatusChart;

    @FXML
    private BarChart<String, Number> reservationsBySpaceChart;

    private ReservationEspaceService reservationService;
    private EspaceService espaceService;
    private ObservableList<ReservationEspace> reservations;
    private static final int ITEMS_PER_PAGE = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        reservationService = new ReservationEspaceService();
        espaceService = new EspaceService();

        // Initialize filter combobox values
        comboFilterStatus.setItems(FXCollections.observableArrayList("Tous", "Confirmée", "En attente", "Annulée", "Terminée", "En cours"
        ));
        comboFilterStatus.getSelectionModel().selectFirst();

        List<Espace> espaces = espaceService.getAll();
        comboFilterEspace.setItems(FXCollections.observableArrayList(espaces));
        comboFilterEspace.setConverter(new javafx.util.StringConverter<Espace>() {
            @Override
            public String toString(Espace espace) {
                if (espace == null) return "Tous les espaces";
                return espace.getNom();
            }

            @Override
            public Espace fromString(String string) {
                return null;
            }
        });

        // Configure table columns
        configureTableColumns();

        // Set up pagination
        paginationReservations.setPageFactory(this::createPage);

        // Load reservations
        refreshReservations();
    }

    private void configureTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        
        colEspace.setCellValueFactory(cellData -> {
            int espaceId = cellData.getValue().getEspaceId();
            Espace espace = espaceService.getById(espaceId);
            String espaceName = espace != null ? espace.getNom() : "Espace inconnu";
            return new SimpleStringProperty(espaceName);
        });
        
        colClient.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmailClient()));
        
        colDateDebut.setCellValueFactory(cellData -> {
            LocalDate dateDebut = cellData.getValue().getDateDebut();
            String formattedDate = dateDebut != null ? formatDate(dateDebut) : "N/A";
            return new SimpleStringProperty(formattedDate);
        });
        
        colDateFin.setCellValueFactory(cellData -> {
            LocalDate dateFin = cellData.getValue().getDateFin();
            String formattedDate = dateFin != null ? formatDate(dateFin) : "N/A";
            return new SimpleStringProperty(formattedDate);
        });
        
        colStatut.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
        
        colPrix.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getMontant()));

        // Configure actions column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("btn-edit");
                btnDelete.getStyleClass().add("btn-delete");
                
                btnEdit.setOnAction(event -> {
                    ReservationEspace reservation = getTableView().getItems().get(getIndex());
                    editReservation(reservation);
                });
                
                btnDelete.setOnAction(event -> {
                    ReservationEspace reservation = getTableView().getItems().get(getIndex());
                    deleteReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private TableView<ReservationEspace> createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, reservations.size());
        
        TableView<ReservationEspace> table = new TableView<>(
                FXCollections.observableArrayList(reservations.subList(fromIndex, toIndex)));
        table.setItems(FXCollections.observableArrayList(
                reservations.subList(fromIndex, toIndex)));
        return tableReservations;
    }

    @FXML
    private void refreshReservations() {
        try {
            List<ReservationEspace> allReservations = reservationService.getAllReservations();
            reservations = FXCollections.observableArrayList(allReservations);
            
            // Update table
            int totalPages = (int) Math.ceil((double) reservations.size() / ITEMS_PER_PAGE);
            paginationReservations.setPageCount(totalPages > 0 ? totalPages : 1);
            tableReservations.setItems(reservations);
            
            // Update statistics
            updateStatistics();
            updateCharts();
            
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les réservations", e.getMessage());
        }
    }

    @FXML
    private void showAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/FormulaireReservation.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh after adding
            refreshReservations();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réservation", e.getMessage());
        }
    }

    @FXML
    private void applyFilters() {
        String status = comboFilterStatus.getValue();
        Espace espace = comboFilterEspace.getValue();
        LocalDate startDate = dateFilterStart.getValue();
        LocalDate endDate = dateFilterEnd.getValue();
        
        List<ReservationEspace> allReservations = reservationService.getAllReservations();
        ObservableList<ReservationEspace> filteredReservations = FXCollections.observableArrayList();
        
        for (ReservationEspace reservation : allReservations) {
            boolean matches = true;
            
            // Apply status filter
            if (!"Tous".equals(status) && !status.equals(reservation.getStatus())) {
                matches = false;
            }
            
            // Apply espace filter
            if (espace != null && reservation.getEspaceId() != espace.getId()) {
                matches = false;
            }
            
            // Apply date filters
            if (startDate != null && endDate != null) {
                LocalDate reservationStart = reservation.getDateDebut();
                LocalDate reservationEnd = reservation.getDateFin();
                
                // Check if reservation period overlaps with filter period
                boolean overlaps = !(reservationEnd != null && reservationEnd.isBefore(startDate) || 
                                   reservationStart != null && reservationStart.isAfter(endDate));
                
                if (!overlaps) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredReservations.add(reservation);
            }
        }
        
        reservations = filteredReservations;
        tableReservations.setItems(reservations);
        
        int totalPages = (int) Math.ceil((double) reservations.size() / ITEMS_PER_PAGE);
        paginationReservations.setPageCount(totalPages > 0 ? totalPages : 1);
    }

    @FXML
    private void clearFilters() {
        comboFilterStatus.getSelectionModel().selectFirst();
        comboFilterEspace.setValue(null);
        dateFilterStart.setValue(null);
        dateFilterEnd.setValue(null);
        
        refreshReservations();
    }

    private void editReservation(ReservationEspace reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/FormulaireReservation.fxml"));
            Parent root = loader.load();
            
            // FormulaireReservationController controller = loader.getController();
            // controller.setReservation(reservation);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh after editing
            refreshReservations();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification", e.getMessage());
        }
    }

    private void deleteReservation(ReservationEspace reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la réservation");
        alert.setContentText("Voulez-vous vraiment supprimer cette réservation ?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                reservationService.delete(reservation.getId());
                refreshReservations();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de supprimer la réservation", e.getMessage());
            }
        }
    }

    private void updateStatistics() {
        // Count total reservations
        int total = reservations.size();
        lblTotalReservations.setText(String.valueOf(total));
        
        // Count active reservations
        long active = reservations.stream()
                .filter(r -> "Confirmée".equals(r.getStatus()))
                .count();
        lblActiveReservations.setText(String.valueOf(active));
        
        // Count upcoming reservations
        long upcoming = reservations.stream()
                .filter(r -> "En attente".equals(r.getStatus()))
                .count();
        lblUpcomingReservations.setText(String.valueOf(upcoming));
        
        // Calculate total revenue
        double revenue = reservations.stream()
                .mapToDouble(ReservationEspace::getMontant)
                .sum();
        lblTotalRevenue.setText(String.format("%.2f DT", revenue));
    }

    private void updateCharts() {
        // Statistiques existantes
        updateMonthlyChart();
        updateStatusChart();
        
        // Nouvelles statistiques
        updatePopularSpacesChart();
    }
    
    private void updateMonthlyChart() {
        // Statistiques par mois
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Long> monthlyStats = reservationService.getMonthlyStats();
        monthlyStats.forEach((month, count) -> 
            series.getData().add(new XYChart.Data<>(month, count)));
        reservationsByMonthChart.getData().clear();
        reservationsByMonthChart.getData().add(series);
    }

    private void updateStatusChart() {
        // Répartition par statut
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Long> statusStats = reservationService.getStatusStats();
        statusStats.forEach((status, count) -> 
            pieData.add(new PieChart.Data(status + " (" + count + ")", count)));
        reservationsByStatusChart.setData(pieData);
    }

    private void updatePopularSpacesChart() {
        // Nouveau graphique des espaces les plus réservés (sans modifier l'existant)
        if (reservationsBySpaceChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Map<String, Long> spaceStats = reservationService.getPopularSpacesStats();
            spaceStats.forEach((space, count) -> 
                series.getData().add(new XYChart.Data<>(space, count)));
            reservationsBySpaceChart.getData().clear();
            reservationsBySpaceChart.getData().add(series);
        }
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
