package com.esprit.controllers;

import com.esprit.modules.utilisateurs.User;
import com.esprit.services.UserDAO;
import com.esprit.services.UserDAOImpl;
import com.esprit.utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML

    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    private UserDAO userDAO;
    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DatabaseUtil.getConnection();
            userDAO = new UserDAOImpl(connection);
        } catch (SQLException e) {
            messageLabel.setText("Erreur de connexion à la base de données");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            User user = userDAO.login(username, password);
            if (user != null) {
                messageLabel.setText("Connexion réussie!");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Charger le tableau de bord
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                Parent dashboardRoot = loader.load();

                // Passer l'utilisateur connecté au contrôleur du tableau de bord
                DashboardController dashboardController = loader.getController();
                dashboardController.setCurrentUser(user);
                dashboardController.initData();

                // Afficher le tableau de bord
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot));
                stage.setTitle("Tableau de bord - Gestion Utilisateurs");
                stage.setMaximized(true);
                stage.show();
            } else {
                messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la connexion: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement du tableau de bord");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}