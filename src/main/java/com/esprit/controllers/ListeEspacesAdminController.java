package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class ListeEspacesAdminController {

    @FXML
    private TextField txtRecherche;

    @FXML
    private TableView<Espace> tableEspace;

    @FXML
    private TableColumn<Espace, String> colNom;

    @FXML
    private TableColumn<Espace, String> colType;

    @FXML
    private TableColumn<Espace, Integer> colCapacite;

    @FXML
    private TableColumn<Espace, String> colLocalisation;

    @FXML
    private TableColumn<Espace, Double> colPrix;

    @FXML
    private TableColumn<Espace, Boolean> colDisponibilite;

    private final EspaceService espaceService;

    private ObservableList<Espace> espacesList = FXCollections.observableArrayList();

    public ListeEspacesAdminController() {
        this.espaceService = new EspaceService(); // Crée une instance du service
    }

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));

        // Enable multiple selection in TableView
        tableEspace.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        loadEspaces();
    }

    private void loadEspaces() {
        List<Espace> espaces = espaceService.getAll();
        espacesList.clear();
        espacesList.addAll(espaces);
        tableEspace.setItems(espacesList);
    }

    @FXML
    void rechercherEspace(ActionEvent event) {
        String searchText = txtRecherche.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            tableEspace.setItems(espacesList);
            return;
        }
        ObservableList<Espace> filteredList = FXCollections.observableArrayList();
        for (Espace espace : espacesList) {
            if (espace.getNom().toLowerCase().contains(searchText) ||
                espace.getType().toLowerCase().contains(searchText) ||
                espace.getLocalisation().toLowerCase().contains(searchText)) {
                filteredList.add(espace);
            }
        }
        tableEspace.setItems(filteredList);
    }

    public void retourMenu(ActionEvent actionEvent) {
        chargerInterface("/MenuEspace.fxml", actionEvent);
    }

    public void goToAjouterEspaceAdmin(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AjouterEspaceAdmin.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void goToConsulterEspacesUser(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ConsulterEspacesUser.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void ModifierEspace(ActionEvent actionEvent) {
        Espace selectedEspace = tableEspace.getSelectionModel().getSelectedItem();
        if (selectedEspace == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Modification");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un espace à modifier.");
            alert.showAndWait();
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ModifierEspaceAdmin.fxml"));
            javafx.scene.Parent root = loader.load();

            com.esprit.controllers.ModifierEspaceAdminController controller = loader.getController();
            controller.setEspace(selectedEspace);

            Stage stage = new Stage();
            stage.setTitle("Modifier Espace");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadEspaces();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ouverture du formulaire de modification.");
            alert.showAndWait();
        }
    }

    public void supprimerEspace(ActionEvent actionEvent) {
        ObservableList<Espace> selectedEspaces = tableEspace.getSelectionModel().getSelectedItems();
        if (selectedEspaces == null || selectedEspaces.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Suppression");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins un espace à supprimer.");
            alert.showAndWait();
            return;
        }

        javafx.scene.control.Alert confirmationAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer définitivement les espaces sélectionnés ?");

        java.util.Optional<javafx.scene.control.ButtonType> result = confirmationAlert.showAndWait();
        if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
            return; // User cancelled deletion
        }

        try {
            for (Espace espace : selectedEspaces) {
                espaceService.delete(espace);
            }
            espacesList.removeAll(selectedEspaces);
            tableEspace.setItems(espacesList);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Suppression");
            alert.setHeaderText(null);
            alert.setContentText("Espaces supprimés avec succès.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression des espaces.");
            alert.showAndWait();
        }
    }

    public void ajouterEspace(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AjouterEspaceAdmin.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Espace");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            loadEspaces();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ouverture du formulaire d'ajout.");
            alert.showAndWait();
        }
    }

    private void chargerInterface(String fxmlPath, ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}