package com.esprit.controllers;

import com.esprit.models.Admin;
import com.esprit.models.Client;
import com.esprit.models.Evenement;
import com.esprit.models.Reservation;
import com.esprit.services.ClientService;
import com.esprit.services.EvenementService;
import com.esprit.services.ReservationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class ReservationsController implements AdminAware {

    @FXML
    private TableView<Reservation> tableView;

    @FXML
    private TableColumn<Reservation, String> colEvent;

    @FXML
    private TableColumn<Reservation, String> colClient;

    @FXML
    private TableColumn<Reservation, String> colDate;

    @FXML
    private TableColumn<Reservation, String> colStatus;

    @FXML
    private TableColumn<Reservation, Void> colActions;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    private ReservationService reservationService = new ReservationService();
    private EvenementService evenementService = new EvenementService();
    private ClientService clientService = new ClientService();
    private Admin currentAdmin;
    private Map<Integer, Evenement> eventCache = new HashMap<>();
    private Map<Integer, Client> clientCache = new HashMap<>();
    private ObservableList<Reservation> reservationsList;
    private FilteredList<Reservation> filteredData;

    @FXML
    public void initialize() {
        // Configure table columns
        colEvent.setCellValueFactory(cellData -> {
            int eventId = cellData.getValue().getId_ev();
            Evenement event = eventCache.get(eventId);
            if (event == null) {
                event = evenementService.rechercherParId(eventId);
                if (event != null) {
                    eventCache.put(eventId, event);
                }
            }
            return new SimpleStringProperty(event != null ? event.getTitle() : "Event #" + eventId);
        });

        colClient.setCellValueFactory(cellData -> {
            int clientId = cellData.getValue().getId_user();
            Client client = clientCache.get(clientId);
            if (client == null) {
                client = clientService.rechercherParId(clientId);
                if (client != null) {
                    clientCache.put(clientId, client);
                }
            }
            return new SimpleStringProperty(client != null ? 
                client.getNom_suser() + " " + client.getPrenom_user() : 
                "Client #" + clientId);
        });

        colDate.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate_res()));
        colStatus.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));

        // Add action buttons to each row
        colActions.setCellFactory(createActionButtonCellFactory());

        // Set up status filter
        statusFilter.getItems().addAll("Tous", "Réservée", "Annulée");
        statusFilter.setValue("Tous");

        // Set up search and filter functionality
        setupSearchAndFilter();

        // Load data
        loadData();
    }

    private void setupSearchAndFilter() {
        reservationsList = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(reservationsList, b -> true);

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });

        // Status filter functionality
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });

        SortedList<Reservation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    private void updateFilter() {
        filteredData.setPredicate(reservation -> {
            String searchText = searchField.getText().toLowerCase();
            String statusFilterValue = statusFilter.getValue();

            // Check if reservation matches search text
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                Evenement event = eventCache.get(reservation.getId_ev());
                Client client = clientCache.get(reservation.getId_user());
                
                matchesSearch = (event != null && event.getTitle().toLowerCase().contains(searchText)) ||
                               (client != null && (client.getNom_suser() + " " + client.getPrenom_user()).toLowerCase().contains(searchText)) ||
                               reservation.getDate_res().toLowerCase().contains(searchText);
            }

            // Check if reservation matches status filter
            boolean matchesStatus = true;
            if (statusFilterValue != null && !statusFilterValue.equals("Tous")) {
                matchesStatus = reservation.getStatus().equals(statusFilterValue);
            }

            return matchesSearch && matchesStatus;
        });
    }

    @Override
    public void setAdmin(Admin admin) {
        this.currentAdmin = admin;
    }

    private Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> createActionButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Reservation, Void> call(TableColumn<Reservation, Void> param) {
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
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            handleModifier(reservation);
                        });

                        btnSupprimer.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            handleSupprimer(reservation);
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
        reservationsList.clear();
        List<Reservation> reservationList = reservationService.rechercher();
        reservationsList.addAll(reservationList);
    }

    @FXML
    void handleAjouter() {
        // TODO: Implement adding new reservation
        showAlert("Information", "La fonctionnalité d'ajout de réservation sera bientôt disponible", Alert.AlertType.INFORMATION);
    }

    private void handleModifier(Reservation reservation) {
        if (reservation == null) {
            showAlert("Erreur", "Veuillez sélectionner une réservation à modifier", Alert.AlertType.WARNING);
            return;
        }

        // Create a custom dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modifier le statut");
        dialog.setHeaderText("Choisir le nouveau statut");

        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create the custom content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label label = new Label("Statut:");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Réservée", "Annulée");
        statusComboBox.setValue(reservation.getStatus());
        statusComboBox.setStyle("-fx-background-color: white; -fx-border-color: #ff8fb3; -fx-border-radius: 5; -fx-padding: 5;");
        statusComboBox.setPrefWidth(200);

        content.getChildren().addAll(label, statusComboBox);
        dialog.getDialogPane().setContent(content);

        // Style the dialog
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setStyle("-fx-background-color: #ff8fb3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-padding: 8 15;");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #ff8fb3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-padding: 8 15;");

        // Convert the result to a string when the confirm button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return statusComboBox.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStatus -> {
            String oldStatus = reservation.getStatus();
            reservation.setStatus(newStatus);
            
            // Update event capacity based on status change
            Evenement event = evenementService.rechercherParId(reservation.getId_ev());
            if (event != null) {
                if (oldStatus.equals("Annulée") && newStatus.equals("Réservée")) {
                    // Decrement places if changing from Annulée to Réservée
                    event.setNbr_places_dispo(event.getNbr_places_dispo() - 1);
                } else if (oldStatus.equals("Réservée") && newStatus.equals("Annulée")) {
                    // Increment places if changing from Réservée to Annulée
                    event.setNbr_places_dispo(event.getNbr_places_dispo() + 1);
                }
                
                // Update both the reservation and event
                evenementService.modifier(event);
                reservationService.modifier(reservation);
                
                // Refresh the table
                loadData();
                
                showAlert("Succès", "Le statut de la réservation a été modifié avec succès", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de trouver l'événement associé", Alert.AlertType.ERROR);
            }
        });
    }

    private void handleSupprimer(Reservation reservation) {
        if (reservation == null) {
            showAlert("Erreur", "Veuillez sélectionner une réservation à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the reservation was active, increment the event capacity
                if (reservation.getStatus().equals("Réservée")) {
                    Evenement event = evenementService.rechercherParId(reservation.getId_ev());
                    if (event != null) {
                        event.setNbr_places_dispo(event.getNbr_places_dispo() + 1);
                        evenementService.modifier(event);
                    }
                }
                
                reservationService.supprimer(reservation);
                loadData();
            }
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 