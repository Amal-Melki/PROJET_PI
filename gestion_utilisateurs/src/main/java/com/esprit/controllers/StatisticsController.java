package com.esprit.controllers;

import com.esprit.modules.utilisateurs.User;
import com.esprit.services.UserDAO;
import com.esprit.services.UserDAOImpl;
import com.esprit.utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class StatisticsController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private PieChart userTypePieChart;

    @FXML
    private BarChart<String, Number> userRoleBarChart;

    private User currentUser;
    private UserDAO userDAO;
    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DatabaseUtil.getConnection();
            userDAO = new UserDAOImpl(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        userInfoLabel.setText("Connecté en tant que: " + user.getUsername() + " (" + user.getRole().getName() + ")");
    }

    public void initData() {
        try {
            // Données pour le graphique en camembert
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            pieChartData.add(new PieChart.Data("Admins", userDAO.compterUtilisateursParRole("admin")));
            pieChartData.add(new PieChart.Data("Clients", userDAO.compterUtilisateursParRole("client")));
            pieChartData.add(new PieChart.Data("Modérateurs", userDAO.compterUtilisateursParRole("moderateur")));
            pieChartData.add(new PieChart.Data("Fournisseurs", userDAO.compterUtilisateursParRole("fournisseur")));
            pieChartData.add(new PieChart.Data("Organisateurs", userDAO.compterUtilisateursParRole("organisateur")));
            userTypePieChart.setData(pieChartData);
            userTypePieChart.setTitle("Répartition des utilisateurs par rôle");

            // Données pour le graphique à barres
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre d'utilisateurs");
            series.getData().add(new XYChart.Data<>("Admins", userDAO.compterUtilisateursParRole("admin")));
            series.getData().add(new XYChart.Data<>("Clients", userDAO.compterUtilisateursParRole("client")));
            series.getData().add(new XYChart.Data<>("Modérateurs", userDAO.compterUtilisateursParRole("moderateur")));
            series.getData().add(new XYChart.Data<>("Fournisseurs", userDAO.compterUtilisateursParRole("fournisseur")));
            series.getData().add(new XYChart.Data<>("Organisateurs", userDAO.compterUtilisateursParRole("organisateur")));

            userRoleBarChart.getData().clear();
            userRoleBarChart.getData().add(series);
            userRoleBarChart.setTitle("Nombre d'utilisateurs par rôle");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/Authentication.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Connexion - Gestion Utilisateurs");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(currentUser);
            dashboardController.initData();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Tableau de bord - Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showUserManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserManagement.fxml"));
            Parent userManagementRoot = loader.load();

            UsersController usersController = loader.getController();
            usersController.setCurrentUser(currentUser);
            usersController.initData();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(userManagementRoot));
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showStatistics(ActionEvent event) {
        // Déjà sur la page de statistiques, ne rien faire
    }

    @FXML
    private void showSettings(ActionEvent event) {
        // Implémentation future pour les paramètres
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres");
        alert.setHeaderText(null);
        alert.setContentText("Cette fonctionnalité sera disponible dans une future mise à jour.");
        alert.showAndWait();
    }
}