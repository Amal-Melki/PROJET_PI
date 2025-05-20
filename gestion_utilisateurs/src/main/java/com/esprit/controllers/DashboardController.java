package com.esprit.controllers;

import com.esprit.services.AdminService;
import com.esprit.services.ClientService;

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

    public DashboardController() {
        adminService = new AdminService();
        clientService = new ClientService();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateUserTypePieChart();

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


} 