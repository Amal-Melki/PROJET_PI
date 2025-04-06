package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.models.User;
import com.esprit.services.AdminService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class UtilisateursController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> colNom;

    @FXML
    private TableColumn<User, String> colPrenom;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, Void> colActions;

    private AdminService adminService = new AdminService();

    @FXML
    public void initialize() {
        // Configure table columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom_suser"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom_user"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email_user"));
        colRole.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                String roleText;
                switch (admin.getRole()) {
                    case 0:
                        roleText = "Super Admin";
                        break;
                    case 1:
                        roleText = "Organisateur";
                        break;
                    case 2:
                        roleText = "Fournisseur";
                        break;
                    case 3:
                        roleText = "Hôte";
                        break;
                    default:
                        roleText = "Admin";
                }
                return new SimpleStringProperty(roleText);
            }
            return new SimpleStringProperty("User");
        });

        // Add action buttons to each row
        colActions.setCellFactory(createActionButtonCellFactory());

        // Load data
        loadData();
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("✏️");
                    private final Button btnSupprimer = new Button("🗑️");
                    private final HBox buttons = new HBox(5, btnModifier, btnSupprimer);

                    {
                        // Style the buttons
                        String buttonStyle = "-fx-background-color: #ff8fb3; -fx-text-fill: white; -fx-font-weight: bold; " +
                                "-fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-cursor: hand;";
                        btnModifier.setStyle(buttonStyle);
                        btnSupprimer.setStyle(buttonStyle);

                        // Add button actions
                        btnModifier.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleModifier(user);
                        });

                        btnSupprimer.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleSupprimer(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    private void loadData() {
        ObservableList<User> users = FXCollections.observableArrayList();
        
        // Load admins
        List<Admin> admins = adminService.rechercher();
        users.addAll(admins);
        
        tableView.setItems(users);
    }

    @FXML
    void handleAjouterAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Administrateur");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh table after adding
            stage.setOnHidden(e -> loadData());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Impossible de charger le formulaire d'ajout", Alert.AlertType.ERROR);
        }
    }

    private void handleModifier(User user) {
        if (user == null) {
            showAlert("Veuillez sélectionner un utilisateur à modifier", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierAdmin.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the admin to modify
            ModifierAdminController controller = loader.getController();
            controller.setAdmin((Admin) user);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un Administrateur");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh table after modifying
            stage.setOnHidden(e -> loadData());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Impossible de charger le formulaire de modification", Alert.AlertType.ERROR);
        }
    }

    private void handleSupprimer(User user) {
        if (user == null) {
            showAlert("Veuillez sélectionner un utilisateur à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (user instanceof Admin) {
                    adminService.supprimer((Admin) user);
                }
                loadData();
            }
        });
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 