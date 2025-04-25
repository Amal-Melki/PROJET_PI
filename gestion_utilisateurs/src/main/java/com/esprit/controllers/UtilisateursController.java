package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.models.Client;
import com.esprit.models.User;
import com.esprit.services.AdminService;
import com.esprit.services.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

public class UtilisateursController implements AdminAware {

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

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> roleFilter;

    private AdminService adminService = new AdminService();
    private ClientService clientService = new ClientService();
    private Admin currentAdmin;
    private ObservableList<User> usersList;
    private FilteredList<User> filteredData;

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
            } else if (user instanceof Client) {
                return new SimpleStringProperty("Client");
            }
            return new SimpleStringProperty("User");
        });

        // Add action buttons to each row
        colActions.setCellFactory(createActionButtonCellFactory());

        // Set up role filter
        roleFilter.getItems().addAll("Tous", "Super Admin", "Organisateur", "Fournisseur", "Hôte", "Client");
        roleFilter.setValue("Tous");

        // Set up search and filter functionality
        setupSearchAndFilter();

        // Load data
        loadData();
    }

    private void setupSearchAndFilter() {
        usersList = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(usersList, b -> true);

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });

        // Role filter functionality
        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    private void updateFilter() {
        filteredData.setPredicate(user -> {
            String searchText = searchField.getText().toLowerCase();
            String roleFilterValue = roleFilter.getValue();

            // Check if user matches search text
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = user.getNom_suser().toLowerCase().contains(searchText) ||
                               user.getPrenom_user().toLowerCase().contains(searchText) ||
                               user.getEmail_user().toLowerCase().contains(searchText);
            }

            // Check if user matches role filter
            boolean matchesRole = true;
            if (roleFilterValue != null && !roleFilterValue.equals("Tous")) {
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
                    matchesRole = roleText.equals(roleFilterValue);
                } else if (user instanceof Client) {
                    matchesRole = roleFilterValue.equals("Client");
                }
            }

            return matchesSearch && matchesRole;
        });
    }

    @Override
    public void setAdmin(Admin admin) {
        this.currentAdmin = admin;
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
        usersList.clear();
        
        // Load admins
        List<Admin> admins = adminService.rechercher();
        usersList.addAll(admins);
        
        // Load clients
        List<Client> clients = clientService.rechercher();
        usersList.addAll(clients);
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
            if (user instanceof Admin) {
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
            } else if (user instanceof Client) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierClient.fxml"));
                Parent root = loader.load();
                
                // Get the controller and set the client to modify
                ModifierClientController controller = loader.getController();
                controller.setClient((Client) user);
                
                Stage stage = new Stage();
                stage.setTitle("Modifier un Client");
                stage.setScene(new Scene(root));
                stage.show();
                
                // Refresh table after modifying
                stage.setOnHidden(e -> loadData());
            }
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
                try {
                    if (user instanceof Admin) {
                        adminService.supprimer((Admin) user);
                    } else if (user instanceof Client) {
                        clientService.supprimer((Client) user);
                    }
                    loadData();
                    showAlert("Utilisateur supprimé avec succès", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 