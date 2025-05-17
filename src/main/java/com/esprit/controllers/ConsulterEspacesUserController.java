package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;
import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;

import java.util.List;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;

public class ConsulterEspacesUserController {

    @FXML
    private TextField txtRecherche;

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private TableView<Espace> tableEspaces;

    // Removed colId as per user request

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
    private TableColumn<Espace, String> colDisponibilite;


    private final ObservableList<Espace> espacesList = FXCollections.observableArrayList();

    private final EspaceService espaceService = new EspaceService();

    @FXML
    void initialize() {
        // Initialize table columns
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));

        addModifierButtonToTable();

        // Load initial data
        loadEspaces();

        // Load types into combo box
        loadTypes();
    }

    private void addModifierButtonToTable() {
        Callback<TableColumn<Espace, Void>, TableCell<Espace, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Espace, Void> call(final TableColumn<Espace, Void> param) {
                final TableCell<Espace, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Modifier");

                    {
                        btn.setStyle("-fx-background-color: #ff8fb3; -fx-text-fill: white; -fx-font-weight: bold;");
                        btn.setOnAction((ActionEvent event) -> {
                            Espace espace = getTableView().getItems().get(getIndex());
                            openModifyEspace(espace, event);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

    }

    private void openModifyEspace(Espace espace, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEspaceAdmin.fxml"));
            Parent root = loader.load();

            ModifierEspaceAdminController controller = loader.getController();
            controller.setEspace(espace);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void reserverEspace(ActionEvent event) {
        Espace selectedEspace = tableEspaces.getSelectionModel().getSelectedItem();
        if (selectedEspace == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Réservation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un espace à réserver.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservationUser.fxml"));
            Parent root = loader.load();

            AjouterReservationUserController controller = loader.getController();
            controller.initData(selectedEspace);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEspaces() {
        espacesList.clear();
        List<Espace> espaces = espaceService.getAll();
        espacesList.addAll(espaces);
        tableEspaces.setItems(espacesList);
    }

    private void loadTypes() {
        // For simplicity, get all espaces and extract distinct types
        List<Espace> espaces = espaceService.getAll();
        ObservableList<String> types = FXCollections.observableArrayList();
        for (Espace e : espaces) {
            if (!types.contains(e.getType())) {
                types.add(e.getType());
            }
        }
        comboType.setItems(types);
    }

    @FXML
    void rechercherEspace(ActionEvent event) {
        String searchText = txtRecherche.getText().toLowerCase().trim();
        String selectedType = comboType.getValue();

        ObservableList<Espace> filteredList = FXCollections.observableArrayList();

        if (searchText.isEmpty() && (selectedType == null || selectedType.isEmpty())) {
            filteredList.addAll(espacesList);
        } else if (searchText.isEmpty()) {
            for (Espace espace : espacesList) {
                if (espace.getType().equals(selectedType)) {
                    filteredList.add(espace);
                }
            }
        } else if (selectedType == null || selectedType.isEmpty()) {
            for (Espace espace : espacesList) {
                if (espace.getNom().toLowerCase().contains(searchText) ||
                    espace.getType().toLowerCase().contains(searchText) ||
                    espace.getLocalisation().toLowerCase().contains(searchText)) {
                    filteredList.add(espace);
                }
            }
        } else {
            for (Espace espace : espacesList) {
                if ((espace.getNom().toLowerCase().contains(searchText) ||
                    espace.getType().toLowerCase().contains(searchText) ||
                    espace.getLocalisation().toLowerCase().contains(searchText)) &&
                    espace.getType().equals(selectedType)) {
                    filteredList.add(espace);
                }
            }
        }
        tableEspaces.setItems(filteredList);
    }

    @FXML
    void retourMenu(ActionEvent event) {
        chargerInterface("MenuEspace.fxml", event);
    }

    private void chargerInterface(String fxml, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/" + fxml)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void voirMesReservations(ActionEvent event) {
        // Navigate to MesReservationsClient.fxml or appropriate reservations view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesReservationsClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}