package com.esprit.controllers;

import com.esprit.models.Client;
import com.esprit.models.Evenement;
import com.esprit.models.Reservation;
import com.esprit.services.EvenementService;
import com.esprit.services.ReservationService;
import com.esprit.services.EmailService;
import com.esprit.services.PDFService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import java.awt.Desktop;
import org.json.JSONObject;
import org.json.JSONArray;

public class EvenementsFrontController implements Initializable {
    @FXML
    private FlowPane eventsContainer;
    
    private EvenementService evenementService;
    private ReservationService reservationService;
    private EmailService emailService;
    private PDFService pdfService;
    private Client currentClient;
    private List<Evenement> evenements;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        evenementService = new EvenementService();
        reservationService = new ReservationService();
        emailService = new EmailService();
        pdfService = new PDFService();
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
        evenements = evenementService.rechercher();
        eventsContainer.getChildren().clear();
        
        for (Evenement event : evenements) {
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
        
        // Weather Button
        Button weatherButton = new Button("Voir la météo");
        weatherButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        weatherButton.setMaxWidth(Double.MAX_VALUE);
        weatherButton.setOnAction(e -> checkWeather(event));
        
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
        
        card.getChildren().addAll(titleLabel, descLabel, dateLabel, locationLabel, placesLabel, weatherButton, actionButton);
        
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
            
            // Send confirmation email
            try {
                emailService.sendReservationConfirmation(
                    currentClient.getEmail_user(),
                    event.getTitle(),
                    event.getDate_debut().toString(),
                    event.getLatitude() + ", " + event.getLongitude()
                );
                System.out.println("Email de confirmation envoyé à : " + currentClient.getEmail_user());
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
                // Continue with the reservation even if email fails
            }
            
            // Generate PDF confirmation
            try {
                pdfService.generateReservationPDF(reservation, currentClient, event);
                System.out.println("PDF de confirmation généré avec succès");
            } catch (Exception e) {
                System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
                // Continue with the reservation even if PDF generation fails
            }
            
            // Update UI
            placesLabel.setText("Places disponibles: " + event.getNbr_places_dispo());
            actionButton.setText("Annuler la réservation");
            actionButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
            actionButton.setOnAction(e -> handleCancelReservation(reservation, event, actionButton, placesLabel));
            
            showAlert("Succès", "Réservation effectuée avec succès. Un email de confirmation a été envoyé et un PDF a été généré.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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

    @FXML
    private void handleCalendarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CalendarView.fxml"));
            Parent calendarView = loader.load();
            
            // Get the controller and set the current client
            CalendarViewController controller = loader.getController();
            if (currentClient != null) {
                controller.setCurrentClient(currentClient);
            }
            
            // Get the current stage
            Stage stage = (Stage) eventsContainer.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(calendarView);
            stage.setScene(scene);
            stage.setTitle("Calendrier des Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement du calendrier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleMapView() {
        // Create a dialog to select an event
        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle("Sélectionner un événement");
        dialog.setHeaderText("Choisissez un événement pour voir son emplacement sur la carte");
        dialog.getDialogPane().setStyle("-fx-background-color: white;");

        // Set the button types
        ButtonType selectButtonType = new ButtonType("Voir sur la carte", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        // Style the buttons
        dialog.getDialogPane().lookupButton(selectButtonType).setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;"
        );
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;"
        );

        // Create the ComboBox for event selection
        ComboBox<Evenement> eventComboBox = new ComboBox<>();
        eventComboBox.setItems(FXCollections.observableArrayList(evenements));
        eventComboBox.setStyle("-fx-background-color: white; -fx-border-color: #2196F3; -fx-border-radius: 5; -fx-padding: 5;");
        eventComboBox.setPrefWidth(300);
        
        eventComboBox.setCellFactory(param -> new ListCell<Evenement>() {
            @Override
            protected void updateItem(Evenement event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getTitle() + " - " + event.getDate_debut());
                }
                setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-padding: 8;");
            }
        });
        
        eventComboBox.setButtonCell(new ListCell<Evenement>() {
            @Override
            protected void updateItem(Evenement event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getTitle() + " - " + event.getDate_debut());
                }
                setStyle("-fx-background-color: white; -fx-text-fill: black;");
            }
        });

        // Create a styled label
        Label label = new Label("Sélectionnez un événement:");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Create a styled container
        VBox content = new VBox(15);
        content.setStyle("-fx-background-color: white; -fx-padding: 20;");
        content.getChildren().addAll(label, eventComboBox);

        // Add the content to the dialog
        dialog.getDialogPane().setContent(content);

        // Convert the result to an event when the select button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return eventComboBox.getValue();
            }
            return null;
        });

        // Show the dialog and handle the result
        Optional<Evenement> result = dialog.showAndWait();
        result.ifPresent(event -> {
            if (event != null) {
                try {
                    // Parse latitude and longitude as doubles
                    double latitude = Double.parseDouble(event.getLatitude());
                    double longitude = Double.parseDouble(event.getLongitude());
                    
                    // Create the Google Maps URL with the event's coordinates
                    String mapsUrl = String.format("https://www.google.com/maps?q=%.6f,%.6f",
                        latitude, longitude);
                    
                    // Open the URL in the default browser
                    Desktop.getDesktop().browse(new URI(mapsUrl));
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Les coordonnées de l'événement sont invalides", Alert.AlertType.ERROR);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible d'ouvrir Google Maps: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void checkWeather(Evenement event) {
        try {
            // Show loading dialog
            ProgressIndicator progress = new ProgressIndicator();
            Dialog<Void> loadingDialog = new Dialog<>();
            loadingDialog.setTitle("Chargement");
            loadingDialog.setHeaderText("Récupération des données météo...");
            loadingDialog.getDialogPane().setContent(progress);
            loadingDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            loadingDialog.show();

            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();
            
            // Build the API URL with the event's coordinates
            String apiKey = "4cdbed78562d5f784e658e637be9d6b0";
            String url = String.format("https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s&units=metric&lang=fr",
                event.getLatitude(), event.getLongitude(), apiKey);

            // Create the request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Close loading dialog
            loadingDialog.close();

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray forecastList = jsonResponse.getJSONArray("list");
                
                // Create weather dialog
                Dialog<Void> weatherDialog = new Dialog<>();
                weatherDialog.setTitle("Météo pour " + event.getTitle());
                weatherDialog.setHeaderText("Prévisions météo pour la durée de l'événement");
                
                // Create main container
                VBox mainContainer = new VBox(20);
                mainContainer.setPadding(new Insets(20));
                mainContainer.setStyle("-fx-background-color: white;");
                
                // Add event info
                Label eventInfoLabel = new Label(String.format("Événement: %s\nDu %s au %s", 
                    event.getTitle(),
                    event.getDate_debut(),
                    event.getDate_fin()));
                eventInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
                
                // Create weather cards container
                VBox weatherCardsContainer = new VBox(15);
                weatherCardsContainer.setStyle("-fx-background-color: white;");
                
                // Get all forecasts for the event duration
                LocalDate startDate = event.getDate_debut();
                LocalDate endDate = event.getDate_fin();
                
                // Group forecasts by date
                Map<LocalDate, List<JSONObject>> forecastsByDate = new HashMap<>();
                
                for (int i = 0; i < forecastList.length(); i++) {
                    JSONObject forecast = forecastList.getJSONObject(i);
                    long forecastTime = forecast.getLong("dt") * 1000;
                    LocalDate forecastDate = java.time.Instant.ofEpochMilli(forecastTime)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                    
                    if (!forecastDate.isBefore(startDate) && !forecastDate.isAfter(endDate)) {
                        forecastsByDate.computeIfAbsent(forecastDate, k -> new ArrayList<>()).add(forecast);
                    }
                }
                
                // Create a card for each day
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    List<JSONObject> dayForecasts = forecastsByDate.get(date);
                    if (dayForecasts != null && !dayForecasts.isEmpty()) {
                        // Get the forecast for noon (or closest available time)
                        JSONObject dayForecast = dayForecasts.stream()
                            .min(Comparator.comparing(f -> {
                                long time = f.getLong("dt") * 1000;
                                LocalDateTime forecastTime = java.time.Instant.ofEpochMilli(time)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDateTime();
                                return Math.abs(forecastTime.getHour() - 12);
                            }))
                            .orElse(dayForecasts.get(0));
                        
                        // Create weather card
                        HBox weatherCard = createWeatherCard(date, dayForecast);
                        weatherCardsContainer.getChildren().add(weatherCard);
                    }
                }
                
                // Wrap weatherCardsContainer in a ScrollPane
                ScrollPane scrollPane = new ScrollPane(weatherCardsContainer);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(350);
                scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
                
                // Add components to main container
                mainContainer.getChildren().addAll(eventInfoLabel, scrollPane);
                
                // Add close button
                ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
                weatherDialog.getDialogPane().getButtonTypes().add(closeButton);
                
                // Style the dialog
                weatherDialog.getDialogPane().setContent(mainContainer);
                weatherDialog.getDialogPane().setStyle("-fx-background-color: white;");
                
                // Style the close button
                Button closeBtn = (Button) weatherDialog.getDialogPane().lookupButton(closeButton);
                closeBtn.setStyle("-fx-background-color: #ff7fa8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;");
                
                weatherDialog.showAndWait();
            } else {
                showAlert("Erreur", "Impossible de récupérer les données météo. Code d'erreur: " + response.statusCode(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la récupération des données météo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private HBox createWeatherCard(LocalDate date, JSONObject forecast) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        // Date section
        VBox dateSection = new VBox(5);
        Label dateLabel = new Label(date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE d MMMM")));
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        dateSection.getChildren().add(dateLabel);
        
        // Weather info section
        VBox weatherInfoBox = new VBox(5);
        JSONObject main = forecast.getJSONObject("main");
        JSONArray weather = forecast.getJSONArray("weather");
        JSONObject weatherData = weather.getJSONObject(0);
        
        Label tempLabel = new Label(String.format("Température: %.1f°C", main.getDouble("temp")));
        Label feelsLikeLabel = new Label(String.format("Ressenti: %.1f°C", main.getDouble("feels_like")));
        Label humidityLabel = new Label(String.format("Humidité: %d%%", main.getInt("humidity")));
        Label descriptionLabel = new Label(weatherData.getString("description"));
        
        tempLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ff7fa8;");
        feelsLikeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        humidityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        weatherInfoBox.getChildren().addAll(tempLabel, feelsLikeLabel, humidityLabel, descriptionLabel);
        
        // Add sections to card
        card.getChildren().addAll(dateSection, weatherInfoBox);
        
        return card;
    }
} 