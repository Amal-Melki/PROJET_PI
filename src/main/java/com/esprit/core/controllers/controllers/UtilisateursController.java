package com.esprit.core.controllers.controllers;

import com.esprit.core.controllers.controllers.AdminAware;
import com.esprit.controllers.ModifierAdminController;
import com.esprit.models.Client;
import com.esprit.models.models.Admin;
import com.esprit.models.models.User;
import com.esprit.services.AdminService;
import com.esprit.services.ClientService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
    private TableColumn<User, Void> colModifier;

    @FXML
    private TableColumn<User, Void> colSupprimer;

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
                        roleText = "Moderateur";
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

        // Add action buttons to Modifier and Supprimer columns
        colModifier.setCellFactory(createModifierButtonCellFactory());
        colSupprimer.setCellFactory(createSupprimerButtonCellFactory());

        // Set up role filter
        roleFilter.getItems().addAll("Tous", "Super Admin", "Organisateur", "Fournisseur", "Moderateur", "Client");
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
                            roleText = "Moderateur";
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

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createModifierButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("✏️");
                    {
                        btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-cursor: hand;");
                        btnModifier.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleModifier(user);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnModifier);
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createSupprimerButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnSupprimer = new Button("🗑️");
                    {
                        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-cursor: hand;");
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
                            setGraphic(btnSupprimer);
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
    void handleAjouter() {
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
        if (!validateUser(user)) {
            return;
        }

        try {
            if (user instanceof Admin) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierAdmin.fxml"));
                Parent root = loader.load();
                
                // Get the controller and set the admin to modify
                ModifierAdminController controller = loader.getController();
                if (user instanceof Admin) {
                    controller.setAdmin((Admin) user);
                } else {
                    showAlert("Erreur", "Seuls les administrateurs peuvent être modifiés ici", Alert.AlertType.ERROR);
                    return;
                }
                
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

    private boolean validateUser(User user) {
        if (user == null) {
            showAlert("Veuillez sélectionner un utilisateur à modifier", Alert.AlertType.WARNING);
            return false;
        }

        // Validate name and surname
        if (user.getNom_suser() == null || user.getNom_suser().isEmpty() ||
            user.getPrenom_user() == null || user.getPrenom_user().isEmpty()) {
            showAlert("Le nom et le prénom ne peuvent pas être vides", Alert.AlertType.ERROR);
            return false;
        }

        if (!user.getNom_suser().matches("^[a-zA-Z\\s]+$") || !user.getPrenom_user().matches("^[a-zA-Z\\s]+$")) {
            showAlert("Le nom et le prénom ne doivent contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        if (user.getNom_suser().length() < 2 || user.getNom_suser().length() > 50 ||
            user.getPrenom_user().length() < 2 || user.getPrenom_user().length() > 50) {
            showAlert("Le nom et le prénom doivent contenir entre 2 et 50 caractères", Alert.AlertType.ERROR);
            return false;
        }

        // Validate email
        if (user.getEmail_user() == null || user.getEmail_user().isEmpty()) {
            showAlert("L'email ne peut pas être vide", Alert.AlertType.ERROR);
            return false;
        }

        if (!user.getEmail_user().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        // Validate password
        if (user.getPassword_user() == null || user.getPassword_user().isEmpty()) {
            showAlert("Le mot de passe ne peut pas être vide", Alert.AlertType.ERROR);
            return false;
        }

        if (user.getPassword_user().length() < 8) {
            showAlert("Le mot de passe doit contenir au moins 8 caractères", Alert.AlertType.ERROR);
            return false;
        }

        if (!user.getPassword_user().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            showAlert("Le mot de passe doit contenir au moins une lettre et un chiffre", Alert.AlertType.ERROR);
            return false;
        }

        // Additional validation for Client
        if (user instanceof Client) {
            Client client = (Client) user;
            if (client.getNumero_tel() <= 0 || 
                String.valueOf(client.getNumero_tel()).length() < 8 || 
                String.valueOf(client.getNumero_tel()).length() > 15) {
                showAlert("Le numéro de téléphone doit être un nombre valide entre 8 et 15 chiffres", Alert.AlertType.ERROR);
                return false;
            }
        }

        return true;
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
                    System.err.println("Erreur lors de la suppression: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Erreur lors de la suppression de l'utilisateur", Alert.AlertType.ERROR);
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