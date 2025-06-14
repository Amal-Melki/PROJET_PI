package com.esprit.core.controllers.controllers;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class AnalysesController implements Initializable {

    @FXML
    private ComboBox<String> comboPeriod;

    @FXML
    private Button btnExport;

    @FXML
    private Button btnRefresh;

    @FXML
    private Label lblOccupancyRate;

    @FXML
    private Label lblOccupancyTrend;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Label lblRevenueTrend;

    @FXML
    private Label lblTotalBookings;

    @FXML
    private Label lblBookingsTrend;

    @FXML
    private Label lblAverageDuration;

    @FXML
    private Label lblDurationTrend;

    @FXML
    private LineChart<String, Number> chartRevenue;

    @FXML
    private BarChart<String, Number> chartOccupancy;

    @FXML
    private TableView<SpacePerformanceData> tableTopSpaces;

    @FXML
    private TableColumn<SpacePerformanceData, Integer> colRank;

    @FXML
    private TableColumn<SpacePerformanceData, String> colSpaceName;

    @FXML
    private TableColumn<SpacePerformanceData, Double> colOccupancyRate;

    @FXML
    private TableColumn<SpacePerformanceData, Double> colRevenue;

    @FXML
    private TableColumn<SpacePerformanceData, Integer> colBookings;

    @FXML
    private TableColumn<SpacePerformanceData, Double> colRating;

    @FXML
    private PieChart chartReservationStatus;

    @FXML
    private LineChart<String, Number> chartForecast;

    @FXML
    private PieChart chartDurationDistribution;

    private ReservationEspaceService reservationService;
    private EspaceService espaceService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        reservationService = ReservationEspaceService.getInstance();
        espaceService = new EspaceService();

        // Set up period combo box
        comboPeriod.setItems(FXCollections.observableArrayList(
                "Derniers 7 jours", "Dernier mois", "Derniers 3 mois", "Derniers 6 mois", "Dernière année", "Tout"
        ));
        comboPeriod.getSelectionModel().select("Dernier mois");
        comboPeriod.setOnAction(e -> refreshData());

        // Configure table columns
        configureTableColumns();

        // Load initial data
        refreshData();
    }

    private void configureTableColumns() {
        colRank.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRank()).asObject());
        colSpaceName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpaceName()));
        colOccupancyRate.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getOccupancyRate()).asObject());
        colRevenue.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getRevenue()).asObject());
        colBookings.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBookings()).asObject());
        colRating.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getRating()).asObject());
    }

    @FXML
    private void refreshData() {
        try {
            // Get data for the selected period
            List<ReservationEspace> reservations = getReservationsForPeriod();
            List<Espace> espaces = espaceService.getAll();

            // Update KPI indicators
            updateKPIs(reservations, espaces);

            // Update charts
            updateRevenueChart(reservations);
            updateOccupancyChart(reservations, espaces);
            updateReservationStatusChart(reservations);
            updateDurationDistributionChart(reservations);
            updateForecastChart();

            // Update top spaces table
            updateTopSpacesTable(reservations, espaces);

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les données d'analyse", e.getMessage());
            e.printStackTrace();
        }
    }

    private List<ReservationEspace> getReservationsForPeriod() {
        List<ReservationEspace> allReservations = reservationService.getAllReservations();
        String selectedPeriod = comboPeriod.getValue();
        
        if ("Tout".equals(selectedPeriod)) {
            return allReservations;
        }
        
        LocalDate startDate = LocalDate.now();
        
        switch (selectedPeriod) {
            case "Derniers 7 jours":
                startDate = startDate.minusDays(7);
                break;
            case "Dernier mois":
                startDate = startDate.minusMonths(1);
                break;
            case "Derniers 3 mois":
                startDate = startDate.minusMonths(3);
                break;
            case "Derniers 6 mois":
                startDate = startDate.minusMonths(6);
                break;
            case "Dernière année":
                startDate = startDate.minusYears(1);
                break;
        }

        LocalDate finalStartDate = startDate;
        return allReservations.stream()
                .filter(r -> r.getDateDebut() != null && r.getDateDebut().isAfter(finalStartDate))
                .collect(Collectors.toList());
    }

    private void updateKPIs(List<ReservationEspace> reservations, List<Espace> espaces) {
        // Calculate occupancy rate
        double totalDays = ChronoUnit.DAYS.between(
                LocalDate.now().minusMonths(1),
                LocalDate.now()
        );
        
        int totalSpaces = espaces.size();
        double totalPossibleDays = totalDays * totalSpaces;
        
        // Get total booked days in period
        double totalBookedDays = reservations.stream()
                .filter(r -> r.getDateDebut() != null && r.getDateFin() != null)
                .mapToDouble(r -> {
                    LocalDate startDate = r.getDateDebut(); 
                    LocalDate endDate = r.getDateFin();     
                    return ChronoUnit.DAYS.between(startDate, endDate) + 1;
                })
                .sum();
        
        double occupancyRate = (totalBookedDays / totalPossibleDays) * 100;
        lblOccupancyRate.setText(String.format("%.1f%%", occupancyRate));
        
        // Set placeholder trend data (would be calculated from historical data)
        lblOccupancyTrend.setText("↑ 5%");
        lblOccupancyTrend.getStyleClass().setAll("kpi-trend-up");
        
        // Calculate total revenue
        double totalRevenue = reservations.stream()
                .mapToDouble(ReservationEspace::getPrixTotal)
                .sum();
        lblTotalRevenue.setText(String.format("%.2f DT", totalRevenue));
        
        // Set placeholder revenue trend
        lblRevenueTrend.setText("↑ 12%");
        lblRevenueTrend.getStyleClass().setAll("kpi-trend-up");
        
        // Count total bookings
        int totalBookings = reservations.size();
        lblTotalBookings.setText(String.valueOf(totalBookings));
        
        // Set placeholder bookings trend
        lblBookingsTrend.setText("↑ 8%");
        lblBookingsTrend.getStyleClass().setAll("kpi-trend-up");
        
        // Calculate average duration
        double averageDuration = reservations.stream()
                .filter(r -> r.getDateDebut() != null && r.getDateFin() != null)
                .mapToDouble(r -> {
                    LocalDate startDate = r.getDateDebut(); 
                    LocalDate endDate = r.getDateFin();     
                    return ChronoUnit.DAYS.between(startDate, endDate) + 1;
                })
                .average()
                .orElse(0);
        
        lblAverageDuration.setText(String.format("%.1f jours", averageDuration));
        
        // Set placeholder duration trend
        lblDurationTrend.setText("→ 0%");
        lblDurationTrend.getStyleClass().setAll("kpi-trend-neutral");
    }

    private void updateRevenueChart(List<ReservationEspace> reservations) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenus par période");
        
        // Group reservations by month
        Map<String, Double> revenueByPeriod = new LinkedHashMap<>();
        
        // Create sample data (in a real app, we'd calculate this from actual reservations)
        revenueByPeriod.put("Jan", 4500.0);
        revenueByPeriod.put("Fév", 5200.0);
        revenueByPeriod.put("Mar", 4800.0);
        revenueByPeriod.put("Avr", 6300.0);
        revenueByPeriod.put("Mai", 5800.0);
        revenueByPeriod.put("Juin", 7200.0);
        
        for (Map.Entry<String, Double> entry : revenueByPeriod.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        chartRevenue.getData().clear();
        chartRevenue.getData().add(series);
    }

    private void updateOccupancyChart(List<ReservationEspace> reservations, List<Espace> espaces) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Taux d'occupation");
        
        // Calculate occupancy per space (simplified version)
        Map<String, Double> occupancyBySpace = new HashMap<>();
        
        // In a real app, this would be calculated from actual reservations
        // For now, we'll use sample data
        occupancyBySpace.put("Salle de Conférence A", 85.0);
        occupancyBySpace.put("Espace Créatif", 72.0);
        occupancyBySpace.put("Salle de Réunion B", 64.0);
        occupancyBySpace.put("Bureau Privatif", 92.0);
        occupancyBySpace.put("Espace Coworking", 78.0);
        
        for (Map.Entry<String, Double> entry : occupancyBySpace.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        chartOccupancy.getData().clear();
        chartOccupancy.getData().add(series);
    }

    private void updateReservationStatusChart(List<ReservationEspace> reservations) {
        // Count reservations by status
        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put("Confirmée", 0);
        statusCounts.put("En attente", 0);
        statusCounts.put("Annulée", 0);
        statusCounts.put("Terminée", 0);
        statusCounts.put("En cours", 0);
        
        for (ReservationEspace reservation : reservations) {
            String status = reservation.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }
        
        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            if (entry.getValue() > 0) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        
        chartReservationStatus.setData(pieChartData);
    }

    private void updateDurationDistributionChart(List<ReservationEspace> reservations) {
        // Count reservations by duration ranges
        Map<String, Integer> durationCounts = new LinkedHashMap<>();
        durationCounts.put("1 jour", 0);
        durationCounts.put("2-3 jours", 0);
        durationCounts.put("4-7 jours", 0);
        durationCounts.put("1-2 semaines", 0);
        durationCounts.put("> 2 semaines", 0);
        
        for (ReservationEspace reservation : reservations) {
            if (reservation.getDateDebut() != null && reservation.getDateFin() != null) {
                LocalDate startDate = reservation.getDateDebut(); 
                LocalDate endDate = reservation.getDateFin();     
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                
                String category;
                if (days == 1) {
                    category = "1 jour";
                } else if (days <= 3) {
                    category = "2-3 jours";
                } else if (days <= 7) {
                    category = "4-7 jours";
                } else if (days <= 14) {
                    category = "1-2 semaines";
                } else {
                    category = "> 2 semaines";
                }
                
                durationCounts.put(category, durationCounts.getOrDefault(category, 0) + 1);
            }
        }
        
        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Integer> entry : durationCounts.entrySet()) {
            if (entry.getValue() > 0) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        
        chartDurationDistribution.setData(pieChartData);
    }

    private void updateForecastChart() {
        XYChart.Series<String, Number> seriesActual = new XYChart.Series<>();
        seriesActual.setName("Réservations Réelles");
        
        XYChart.Series<String, Number> seriesForecast = new XYChart.Series<>();
        seriesForecast.setName("Prévisions");
        
        // Sample data for demonstration
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep"};
        int[] actualValues = {12, 18, 15, 22, 24, 28, 30, 0, 0};
        int[] forecastValues = {12, 18, 15, 22, 24, 28, 30, 33, 36};
        
        for (int i = 0; i < months.length; i++) {
            if (actualValues[i] > 0) {
                seriesActual.getData().add(new XYChart.Data<>(months[i], actualValues[i]));
            }
            seriesForecast.getData().add(new XYChart.Data<>(months[i], forecastValues[i]));
        }
        
        chartForecast.getData().clear();
        chartForecast.getData().addAll(seriesActual, seriesForecast);
    }

    private void updateTopSpacesTable(List<ReservationEspace> reservations, List<Espace> espaces) {
        Map<Integer, List<ReservationEspace>> reservationsBySpace = reservations.stream()
                .collect(Collectors.groupingBy(ReservationEspace::getEspaceId));
        
        List<SpacePerformanceData> performanceDataList = new ArrayList<>();
        int rank = 1;
        
        for (Espace espace : espaces) {
            List<ReservationEspace> spaceReservations = reservationsBySpace.getOrDefault(espace.getId(), new ArrayList<>());
            
            double revenue = spaceReservations.stream()
                    .mapToDouble(ReservationEspace::getPrixTotal)
                    .sum();
            
            int bookings = spaceReservations.size();
            
            double occupancyRate = calculateOccupancyRate(espace, spaceReservations);
            
            // Simulate rating (would come from actual ratings in a real app)
            double rating = 3.5 + Math.random() * 1.5;
            
            SpacePerformanceData data = new SpacePerformanceData(
                    rank++, 
                    espace.getNom(), 
                    occupancyRate, 
                    revenue, 
                    bookings, 
                    rating
            );
            
            performanceDataList.add(data);
        }
        
        // Sort by revenue descending
        performanceDataList.sort(Comparator.comparing(SpacePerformanceData::getRevenue).reversed());
        
        // Update ranks after sorting
        for (int i = 0; i < performanceDataList.size(); i++) {
            performanceDataList.get(i).setRank(i + 1);
        }
        
        tableTopSpaces.setItems(FXCollections.observableArrayList(performanceDataList));
    }

    private double calculateOccupancyRate(Espace espace, List<ReservationEspace> reservations) {
        // In a real app, this would be a more sophisticated calculation
        // considering the actual days booked versus available days
        return reservations.isEmpty() ? 0 : 60 + Math.random() * 30;
    }

    @FXML
    private void exportData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export des données");
        alert.setHeaderText("Fonctionnalité en cours de développement");
        alert.setContentText("L'export des données sera disponible dans une prochaine version.");
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner class for table data
    public static class SpacePerformanceData {
        private int rank;
        private final String spaceName;
        private final double occupancyRate;
        private final double revenue;
        private final int bookings;
        private final double rating;

        public SpacePerformanceData(int rank, String spaceName, double occupancyRate, double revenue, int bookings, double rating) {
            this.rank = rank;
            this.spaceName = spaceName;
            this.occupancyRate = occupancyRate;
            this.revenue = revenue;
            this.bookings = bookings;
            this.rating = rating;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getSpaceName() {
            return spaceName;
        }

        public double getOccupancyRate() {
            return occupancyRate;
        }

        public double getRevenue() {
            return revenue;
        }

        public int getBookings() {
            return bookings;
        }

        public double getRating() {
            return rating;
        }
    }
}
