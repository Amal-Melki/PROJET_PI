package com.esprit.controllers;

import com.esprit.services.AdminService;
import com.esprit.services.ClientService;
import com.esprit.services.EvenementService;
import com.esprit.services.ReservationService;
import com.esprit.models.Evenement;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class DashboardController implements Initializable {

    @FXML
    private PieChart userTypePieChart;

    @FXML
    private BarChart<String, Number> eventsBarChart;

    @FXML
    private PieChart reservationStatusPieChart;

    @FXML
    private CategoryAxis eventsCategoryAxis;

    @FXML
    private NumberAxis eventsNumberAxis;

    private AdminService adminService;
    private ClientService clientService;
    private EvenementService evenementService;
    private ReservationService reservationService;

    public DashboardController() {
        adminService = new AdminService();
        clientService = new ClientService();
        evenementService = new EvenementService();
        reservationService = new ReservationService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateUserTypePieChart();
        populateEventsBarChart();
        populateReservationStatusPieChart();
    }

    private void populateUserTypePieChart() {
        int adminCount = adminService.rechercher().size();
        int clientCount = clientService.rechercher().size();
        System.out.println("Admin count: " + adminCount + ", Client count: " + clientCount);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Admin", adminCount),
            new PieChart.Data("Client", clientCount)
        );
        userTypePieChart.setData(pieChartData);
    }

    private void populateEventsBarChart() {
        List<Evenement> events = evenementService.rechercher();
        List<Integer> reservedEventIds = reservationService.rechercher().stream()
                .map(r -> r.getId_ev())
                .distinct()
                .toList();

        int reservedCount = (int) events.stream().filter(e -> reservedEventIds.contains(e.getId_ev())).count();
        int nonReservedCount = (int) events.stream().filter(e -> !reservedEventIds.contains(e.getId_ev())).count();
        System.out.println("Reserved events: " + reservedCount + ", Non-reserved events: " + nonReservedCount);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Réservés", reservedCount));
        series.getData().add(new XYChart.Data<>("Non Réservés", nonReservedCount));
        eventsBarChart.getData().clear();
        eventsBarChart.getData().add(series);
    }

    private void populateReservationStatusPieChart() {
        int confirmedCount = (int) reservationService.rechercher().stream().filter(r -> r.getStatus().equals("Confirmée")).count();
        int pendingCount = (int) reservationService.rechercher().stream().filter(r -> r.getStatus().equals("En attente")).count();
        int cancelledCount = (int) reservationService.rechercher().stream().filter(r -> r.getStatus().equals("Annulée")).count();
        System.out.println("Confirmed: " + confirmedCount + ", Pending: " + pendingCount + ", Cancelled: " + cancelledCount);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Confirmée", confirmedCount),
            new PieChart.Data("En attente", pendingCount),
            new PieChart.Data("Annulée", cancelledCount)
        );
        reservationStatusPieChart.setData(pieChartData);
    }
} 