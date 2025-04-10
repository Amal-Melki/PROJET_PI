package com.esprit.controllers;

import com.esprit.models.Client;
import com.esprit.models.Evenement;
import com.esprit.models.Reservation;
import com.esprit.services.EvenementService;
import com.esprit.services.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EvenementsFrontController implements Initializable {
    @FXML
    private FlowPane eventsContainer;
    
    private EvenementService evenementService;
    private ReservationService reservationService;
    private Client currentClient;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        evenementService = new EvenementService();
        reservationService = new ReservationService();
        System.out.println("EvenementsFrontController initialized. Current client: " + (currentClient != null ? currentClient.getId_user() : "null"));
        loadEvents();
    }
    
    public void setCurrentClient(Client client) {
        this.currentClient = client;
        System.out.println("Current client set to: " + (client != null ? client.getId_user() : "null"));
        // Reload events to update the UI with the new client information
        loadEvents();
    }
    
    private void loadEvents() {
        System.out.println("Loading events. Current client: " + (currentClient != null ? currentClient.getId_user() : "null"));
        List<Evenement> events = evenementService.rechercher();
        eventsContainer.getChildren().clear();
        
        for (Evenement event : events) {
            VBox card = createEventCard(event);
            eventsContainer.getChildren().add(card);
        }
    }
    
    private VBox createEventCard(Evenement event) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        
        // Event Title
        Label titleLabel = new Label(event.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Event Description
        Label descLabel = new Label(event.getDescription_ev());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px;");
        
        // Event Date
        Label dateLabel = new Label("Date: " + event.getDate_debut().toString());
        dateLabel.setStyle("-fx-font-size: 14px;");
        
        // Event Location
        Label locationLabel = new Label("Lieu: " + event.getLatitude() + ", " + event.getLongitude());
        locationLabel.setStyle("-fx-font-size: 14px;");
        
        // Available Places
        Label placesLabel = new Label("Places disponibles: " + event.getNbr_places_dispo());
        placesLabel.setStyle("-fx-font-size: 14px;");
        
        // Reserve/Cancel Button
        Button actionButton = new Button("Réserver");
        actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        actionButton.setMaxWidth(Double.MAX_VALUE);
        
        // Check if user already has a reservation for this event
        final Reservation userReservation;
        if (currentClient != null) {
            List<Reservation> userReservations = reservationService.rechercherParClient(currentClient.getId_user());
            System.out.println("Checking reservations for user " + currentClient.getId_user() + " for event " + event.getId_ev());
            System.out.println("Found " + userReservations.size() + " reservations");
            
            // Find the most recent non-cancelled reservation
            userReservation = userReservations.stream()
                .filter(res -> res.getId_ev() == event.getId_ev() && !"Annulée".equals(res.getStatus()))
                .peek(res -> System.out.println("Found active reservation: ID=" + res.getId_res() + ", Status=" + res.getStatus()))
                .findFirst()
                .orElse(null);
                
            System.out.println("Final reservation status for event " + event.getId_ev() + ": " + (userReservation != null ? userReservation.getStatus() : "null"));
        } else {
            userReservation = null;
            System.out.println("No current client for event " + event.getId_ev());
        }
        
        // Set button state based on reservation status
        if (event.getNbr_places_dispo() <= 0) {
            actionButton.setDisable(true);
            actionButton.setText("Complet");
            actionButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold;");
        } else if (userReservation != null) {
            System.out.println("Setting button state for event " + event.getId_ev() + " with reservation status: " + userReservation.getStatus());
            actionButton.setText("Annuler la réservation");
            actionButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
            actionButton.setOnAction(e -> handleCancelReservation(userReservation, event, actionButton, placesLabel));
        } else {
            System.out.println("No active reservation found for event " + event.getId_ev());
            actionButton.setText("Réserver");
            actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            actionButton.setOnAction(e -> handleReservation(event, actionButton, placesLabel));
        }
        
        card.getChildren().addAll(titleLabel, descLabel, dateLabel, locationLabel, placesLabel, actionButton);
        
        return card;
    }
    
    private void handleReservation(Evenement event, Button actionButton, Label placesLabel) {
        if (currentClient == null) {
            showAlert("Erreur", "Vous devez être connecté pour réserver un événement.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Check if user already has a reservation for this event
            List<Reservation> userReservations = reservationService.rechercherParClient(currentClient.getId_user());
            for (Reservation res : userReservations) {
                if (res.getId_ev() == event.getId_ev() && !"Annulée".equals(res.getStatus())) {
                    showAlert("Erreur", "Vous avez déjà réservé cet événement.", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            // Create new reservation
            Reservation reservation = new Reservation();
            reservation.setId_user(currentClient.getId_user());
            reservation.setId_ev(event.getId_ev());
            reservation.setDate_res(LocalDate.now().toString());
            reservation.setStatus("Réservée");
            
            // Add reservation and get the generated ID
            reservationService.ajouter(reservation);
            System.out.println("Nouvelle réservation créée avec ID: " + reservation.getId_res());
            
            // Update event places
            event.setNbr_places_dispo(event.getNbr_places_dispo() - 1);
            evenementService.modifier(event);
            
            // Update UI
            placesLabel.setText("Places disponibles: " + event.getNbr_places_dispo());
            actionButton.setText("Annuler la réservation");
            actionButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
            actionButton.setOnAction(e -> handleCancelReservation(reservation, event, actionButton, placesLabel));
            
            showAlert("Succès", "Réservation effectuée avec succès!", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Erreur", "Une erreur est survenue lors de la réservation: " + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }
    
    private void handleCancelReservation(Reservation reservation, Evenement event, Button actionButton, Label placesLabel) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                System.out.println("Tentative d'annulation de la réservation ID: " + reservation.getId_res());
                
                // Update reservation status to "annulee"
                reservation.setStatus("Annulée");
                reservationService.modifier(reservation);
                
                // Update event places (increment by 1)
                event.setNbr_places_dispo(event.getNbr_places_dispo() + 1);
                evenementService.modifier(event);
                
                // Update UI
                placesLabel.setText("Places disponibles: " + event.getNbr_places_dispo());
                actionButton.setText("Réserver");
                actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                actionButton.setOnAction(e -> handleReservation(event, actionButton, placesLabel));
                
                showAlert("Succès", "Réservation annulée avec succès!", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Erreur", "Une erreur est survenue lors de l'annulation: " + ex.getMessage(), Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 