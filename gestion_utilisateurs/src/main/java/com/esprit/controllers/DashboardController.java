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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Text totalUsersText;

    @FXML
    private Text adminCountText;

    @FXML
    private Text clientCountText;

    @FXML
    private PieChart userTypeChart;

    @FXML
    private TableView<User> recentUsersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    private User currentUser;
    private UserDAO userDAO;
    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DatabaseUtil.getConnection();
            userDAO = new UserDAOImpl(connection);

            // Configuration des colonnes de la table
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            roleColumn.setCellValueFactory(cellData -> {
                String roleName = cellData.getValue().getRole().getName();
                return javafx.beans.binding.Bindings.createStringBinding(() -> roleName);
            });

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
            // Nombre total d'utilisateurs
            int totalUsers = userDAO.getNombreTotalUtilisateurs();
            totalUsersText.setText(String.valueOf(totalUsers));

            // Nombre d'administrateurs
            int adminCount = userDAO.compterUtilisateursParRole("admin");
            adminCountText.setText(String.valueOf(adminCount));

            // Nombre de clients
            int clientCount = userDAO.compterUtilisateursParRole("client");
            clientCountText.setText(String.valueOf(clientCount));

            // Graphique en camembert
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            pieChartData.add(new PieChart.Data("Admins", adminCount));
            pieChartData.add(new PieChart.Data("Clients", clientCount));
            pieChartData.add(new PieChart.Data("Modérateurs", userDAO.compterUtilisateursParRole("moderateur")));
            pieChartData.add(new PieChart.Data("Fournisseurs", userDAO.compterUtilisateursParRole("fournisseur")));
            pieChartData.add(new PieChart.Data("Organisateurs", userDAO.compterUtilisateursParRole("organisateur")));
            userTypeChart.setData(pieChartData);

            // Tableau des utilisateurs récents
            List<User> allUsers = userDAO.getTousLesUtilisateurs();
            ObservableList<User> users = FXCollections.observableArrayList(allUsers);
            recentUsersTable.setItems(users);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Charger l'écran de connexion
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
        // Déjà sur le tableau de bord, ne rien faire
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
            Parent statisticsRoot = loader.load();

            StatisticsController statisticsController = loader.getController();
            statisticsController.setCurrentUser(currentUser);
            statisticsController.initData();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(statisticsRoot));
            stage.setTitle("Statistiques - Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showSettings(ActionEvent event) {
        // Implémentation future pour les paramètres
    }
}

