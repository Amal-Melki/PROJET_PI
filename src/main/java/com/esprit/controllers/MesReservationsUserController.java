package com.esprit.controllers;

import com.esprit.modules.ReservationEspace;
import com.esprit.services.ReservationEspaceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MesReservationsUserController {

    @FXML
    private Button btnRetour;
    @FXML
    private Button btnAnnulerReservation;
    @FXML
    private Button btnVoirDetails;

    @FXML
    private TableView<ReservationEspace> tableReservations;
    @FXML
    private TableColumn<ReservationEspace, Integer> colReservationId;
    @FXML
    private TableColumn<ReservationEspace, Integer> colEspace;
    @FXML
    private TableColumn<ReservationEspace, String> colDateDebut;
    @FXML
    private TableColumn<ReservationEspace, String> colDateFin;
    // Removed colStatus as ReservationEspace has no 'status' property
    // @FXML
    // private TableColumn<ReservationEspace, String> colStatus;

    private ReservationEspaceService reservationService = new ReservationEspaceService();

    @FXML
    public void initialize() {
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colEspace.setCellValueFactory(new PropertyValueFactory<>("espaceId"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        // colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadReservations();
    }

    private void loadReservations() {
        ObservableList<ReservationEspace> reservations = FXCollections.observableArrayList(reservationService.getAll());
        tableReservations.setItems(reservations);
    }

    @FXML
    void retourConsulterEspaces(javafx.event.ActionEvent event) {
        chargerInterface("ConsulterEspacesUser.fxml", event);
    }

    @FXML
    void annulerReservation() {
        ReservationEspace selectedReservation = tableReservations.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            reservationService.delete(selectedReservation.getReservationId());

            // Send cancellation email
            String userEmail = selectedReservation.getUser();
            String emailSubject = "Annulation de votre réservation";
            String emailBody = "Bonjour,\n\nVotre réservation avec l'ID " + selectedReservation.getReservationId() +
                    " a été annulée avec succès.\n\nCordialement,\nL'équipe de gestion des espaces.";

            com.esprit.services.EmailService emailService = new com.esprit.services.EmailService();
            try {
                emailService.sendEmail(userEmail, emailSubject, emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }

            loadReservations();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Annulation");
            alert.setHeaderText(null);
            alert.setContentText("Réservation annulée avec succès et email de confirmation envoyé.");
            alert.showAndWait();
        } else {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Annulation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une réservation à annuler.");
            alert.showAndWait();
        }
    }

    @FXML
    void voirDetails(javafx.event.ActionEvent event) {
        ReservationEspace selectedReservation = tableReservations.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/DetailsReservationUser.fxml"));
                javafx.scene.Parent root = loader.load();

                DetailsReservationUserController controller = loader.getController();
                controller.initialize(
                        "Nom Espace", // You should replace with actual espace name
                        "Type Espace", // Replace with actual type
                        10, // Replace with actual capacity
                        "Localisation", // Replace with actual location
                        100.0, // Replace with actual price
                        "ID123", // Replace with actual reservation ID
                        selectedReservation.getDateDebut().toString(),
                        selectedReservation.getDateFin().toString(),
                        "Confirmé" // Replace with actual status
                );

                javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Détails");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une réservation pour voir les détails.");
            alert.showAndWait();
        }
    }

    private void chargerInterface(String fxml, javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/" + fxml));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
