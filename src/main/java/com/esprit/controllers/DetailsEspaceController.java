package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetailsEspaceController implements Initializable {

    @FXML
    private TableView<Espace> tableEspace;
    @FXML
    private TableColumn<Espace, Integer> colId;
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

    private EspaceService espaceService = new EspaceService();
    private ObservableList<Espace> espaceList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTable();
        loadEspaces();
    }

    private void configureTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));

        // Format the availability column to show "Disponible" or "Non disponible"
        colDisponibilite.setCellFactory(column -> {
            return new TableCell<Espace, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item ? "Disponible" : "Non disponible");
                    }
                }
            };
        });
    }

    private void loadEspaces() {
        List<Espace> espaces = espaceService.getAll();
        espaceList.setAll(espaces);
        tableEspace.setItems(espaceList);
    }

    @FXML
    private void retourMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuEspace.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void enregistrerEspace(ActionEvent event) {
        // TODO: Implémenter la fonctionnalité pour enregistrer les modifications d'un espace
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Fonctionnalité non disponible");
        alert.setContentText("Cette fonctionnalité sera implémentée dans une prochaine version.");
        alert.showAndWait();
    }

    @FXML
    private void supprimerEspace(ActionEvent event) {
        Espace selectedEspace = tableEspace.getSelectionModel().getSelectedItem();
        if (selectedEspace == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun espace sélectionné");
            alert.setContentText("Veuillez sélectionner un espace à supprimer.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer la suppression");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'espace '" + selectedEspace.getNom() + "' ?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            espaceService.delete(selectedEspace.getId());
            loadEspaces(); // Recharger la liste des espaces

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Espace supprimé");
            successAlert.setContentText("L'espace a été supprimé avec succès.");
            successAlert.showAndWait();
        }
    }
}