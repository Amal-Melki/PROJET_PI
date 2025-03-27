package com.esprit.controllers;

import com.esprit.modules.utilisateurs.User;
import com.esprit.services.UserDAO;
import com.esprit.services.UserDAOImpl;
import com.esprit.utils.DatabaseUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsersController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> searchTypeComboBox;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Void> detailsColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    private User currentUser;
    private UserDAO userDAO;
    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DatabaseUtil.getConnection();
            userDAO = new UserDAOImpl(connection);

            // Configuration des options de recherche
            searchTypeComboBox.setItems(FXCollections.observableArrayList(
                    "Nom d'utilisateur", "Email", "Rôle"));
            searchTypeComboBox.getSelectionModel().selectFirst();

            // Configuration des colonnes de la table
            idColumn.setCellValueFactory(param ->
                    javafx.beans.binding.Bindings.createObjectBinding(() -> param.getValue().getId()));
            usernameColumn.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue().getUsername()));
            emailColumn.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue().getEmail()));
            roleColumn.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue().getRole().getName()));

            // Colonne de détails
            detailsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button detailsButton = new Button("Détails");

                {
                    detailsButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        showUserDetails(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(detailsButton);
                    }
                }
            });

            // Colonne d'actions
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");
                private final HBox buttonsBox = new HBox(5, editButton, deleteButton);

                {
                    editButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        editUser(user);
                    });

                    deleteButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        deleteUser(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(buttonsBox);
                    }
                }
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
        refreshTable();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/Authentication.fxml"));
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Connexion - Gestion Utilisateurs");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            String searchText = searchField.getText().trim();
            String searchType = searchTypeComboBox.getValue();

            if (searchText.isEmpty()) {
                refreshTable();
                return;
            }

            List<User> users;
            if (searchType.equals("Nom d'utilisateur")) {
                users = userDAO.rechercherParNom(searchText);
            } else if (searchType.equals("Email")) {
                User user = userDAO.rechercherParEmail(searchText);
                users = user != null ? List.of(user) : List.of();
            } else if (searchType.equals("Rôle")) {
                users = userDAO.getUtilisateursParRole(searchText);
            } else {
                users = userDAO.getTousLesUtilisateurs();
            }

            usersTable.setItems(FXCollections.observableArrayList(users));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de recherche", e.getMessage());
        }
    }

    @FXML
    private void refreshTable() {
        try {
            List<User> users = userDAO.getTousLesUtilisateurs();
            usersTable.setItems(FXCollections.observableArrayList(users));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUser(null); // Mode ajout

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'ouverture du formulaire", e.getMessage());
        }
    }

    private void editUser(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUser(user); // Mode édition

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier un utilisateur");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'ouverture du formulaire", e.getMessage());
        }
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + user.getUsername() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.supprimerUtilisateur(user.getId());
                refreshTable();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", e.getMessage());
            }
        }
    }

    private void showUserDetails(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'utilisateur");
        alert.setHeaderText("Informations sur " + user.getUsername());

        String details = "ID: " + user.getId() + "\n" +
                "Nom d'utilisateur: " + user.getUsername() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Rôle: " + user.getRole().getName() + "\n";

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(currentUser);
            dashboardController.initData();

            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Tableau de bord - Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showUserManagement(ActionEvent event) {
        // Déjà sur la page de gestion des utilisateurs
    }

    @FXML
    private void showStatistics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
            Parent statisticsRoot = loader.load();

            StatisticsController statisticsController = loader.getController();
            statisticsController.setCurrentUser(currentUser);
            statisticsController.initData();

            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.setScene(new Scene(statisticsRoot));
            stage.setTitle("Statistiques - Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showSettings(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres");
        alert.setHeaderText(null);
        alert.setContentText("Cette fonctionnalité sera disponible dans une future mise à jour.");
        alert.showAndWait();
    }
}