package com.esprit.controllers;

import com.esprit.modules.utilisateurs.*;
import com.esprit.services.UserDAO;
import com.esprit.services.UserDAOImpl;
import com.esprit.utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserFormController {

    @FXML
    private Text formTitle;

    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private HBox passwordContainer;

    @FXML
    private HBox clientFieldsContainer;

    @FXML
    private TextField addressField;

    @FXML
    private HBox fournisseurFieldsContainer;

    @FXML
    private TextField companyNameField;

    @FXML
    private HBox organisateurFieldsContainer;

    @FXML
    private TextField organizationNameField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

    private User currentUser;
    private boolean isEditMode = false;
    private UserDAO userDAO;
    private Connection connection;
    private Map<String, Role> roles = new HashMap<>();

    @FXML
    public void initialize() {
        try {
            connection = DatabaseUtil.getConnection();
            userDAO = new UserDAOImpl(connection);

            // Initialiser les rôles
            roles.put("admin", new Role(1, "admin"));
            roles.put("client", new Role(2, "client"));
            roles.put("moderateur", new Role(3, "moderateur"));
            roles.put("fournisseur", new Role(4, "fournisseur"));
            roles.put("organisateur", new Role(5, "organisateur"));

            // Configurer le ComboBox des types d'utilisateurs
            userTypeComboBox.setItems(FXCollections.observableArrayList(
                    "admin", "client", "moderateur", "fournisseur", "organisateur"));

            userTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateFormFields(newVal);
                }
            });

            // Par défaut, sélectionner "client"
            userTypeComboBox.getSelectionModel().select("client");

        } catch (SQLException e) {
            showError("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        this.isEditMode = (user != null);

        if (isEditMode) {
            formTitle.setText("Modifier un utilisateur");
            passwordContainer.setVisible(false);
            passwordContainer.setManaged(false);

            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());

            String userType = user.getRole().getName();
            userTypeComboBox.getSelectionModel().select(userType);
            userTypeComboBox.setDisable(true); // Ne pas permettre de changer le type lors de l'édition

            if (user instanceof Client) {
                addressField.setText(((Client) user).getAdresse());
            } else if (user instanceof Fournisseur) {
                companyNameField.setText(((Fournisseur) user).getNomEntreprise());
            } else if (user instanceof Organisateur) {
                organizationNameField.setText(((Organisateur) user).getNomOrganisation());
            }
        } else {
            formTitle.setText("Ajouter un utilisateur");
            passwordContainer.setVisible(true);
            passwordContainer.setManaged(true);
            userTypeComboBox.setDisable(false);
        }
    }

    private void updateFormFields(String userType) {
        // Cacher tous les conteneurs de champs spécifiques
        clientFieldsContainer.setVisible(false);
        clientFieldsContainer.setManaged(false);
        fournisseurFieldsContainer.setVisible(false);
        fournisseurFieldsContainer.setManaged(false);
        organisateurFieldsContainer.setVisible(false);
        organisateurFieldsContainer.setManaged(false);

        // Afficher les champs spécifiques en fonction du type d'utilisateur
        switch (userType) {
            case "client":
                clientFieldsContainer.setVisible(true);
                clientFieldsContainer.setManaged(true);
                break;
            case "fournisseur":
                fournisseurFieldsContainer.setVisible(true);
                fournisseurFieldsContainer.setManaged(true);
                break;
            case "organisateur":
                organisateurFieldsContainer.setVisible(true);
                organisateurFieldsContainer.setManaged(true);
                break;
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            String username = usernameField.getText();
            String email = emailField.getText();
            String userType = userTypeComboBox.getValue();
            Role role = roles.get(userType);

            User user;

            if (isEditMode) {
                // Mode édition
                user = currentUser;
                user.setUsername(username);
                user.setEmail(email);

                switch (userType) {
                    case "client":
                        ((Client) user).setAdresse(addressField.getText());
                        break;
                    case "fournisseur":
                        ((Fournisseur) user).setNomEntreprise(companyNameField.getText());
                        break;
                    case "organisateur":
                        ((Organisateur) user).setNomOrganisation(organizationNameField.getText());
                        break;
                }

                userDAO.modifierUtilisateur(user);
            } else {
                // Mode ajout
                String password = passwordField.getText();

                switch (userType) {
                    case "admin":
                        user = new Admin(0, username, password, email, role);
                        break;
                    case "client":
                        user = new Client(0, username, password, email, role, addressField.getText());
                        break;
                    case "moderateur":
                        user = new Moderateur(0, username, password, email, role);
                        break;
                    case "fournisseur":
                        user = new Fournisseur(0, username, password, email, role, companyNameField.getText());
                        break;
                    case "organisateur":
                        user = new Organisateur(0, username, password, email, role, organizationNameField.getText());
                        break;
                    default:
                        showError("Type d'utilisateur non valide");
                        return;
                }

                userDAO.ajouterUtilisateur(user);
            }

            // Fermer la fenêtre après la sauvegarde
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showError("Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        // Réinitialiser le message d'erreur
        errorLabel.setVisible(false);

        // Vérifier les champs obligatoires
        if (usernameField.getText().isEmpty()) {
            showError("Le nom d'utilisateur est obligatoire");
            return false;
        }

        if (!isEditMode && passwordField.getText().isEmpty()) {
            showError("Le mot de passe est obligatoire");
            return false;
        }

        if (emailField.getText().isEmpty()) {
            showError("L'email est obligatoire");
            return false;
        }

        String userType = userTypeComboBox.getValue();

        if (userType.equals("client") && addressField.getText().isEmpty()) {
            showError("L'adresse est obligatoire pour un client");
            return false;
        }

        if (userType.equals("fournisseur") && companyNameField.getText().isEmpty()) {
            showError("Le nom de l'entreprise est obligatoire pour un fournisseur");
            return false;
        }

        if (userType.equals("organisateur") && organizationNameField.getText().isEmpty()) {
            showError("Le nom de l'organisation est obligatoire pour un organisateur");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}