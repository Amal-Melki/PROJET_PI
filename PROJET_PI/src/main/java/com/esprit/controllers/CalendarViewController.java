package com.esprit.controllers;

import com.esprit.models.Evenement;
import com.esprit.models.Client;
import com.esprit.services.EvenementService;
import com.esprit.services.GoogleCalendarService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CalendarViewController implements Initializable {
    @FXML
    private GridPane calendarGrid;
    
    @FXML
    private Label currentMonthLabel;
    
    @FXML
    private VBox upcomingEventsContainer;
    
    private EvenementService evenementService;
    private GoogleCalendarService googleCalendarService;
    private List<Evenement> allEvents;
    private YearMonth currentYearMonth;
    private LocalDate selectedDate;
    private Client currentClient;
    
    public void setCurrentClient(Client client) {
        this.currentClient = client;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        evenementService = new EvenementService();
        googleCalendarService = new GoogleCalendarService();
        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        
        try {
            // Authorize Google Calendar
            googleCalendarService.authorize();
            
            // Load events and sync with calendar
            allEvents = evenementService.rechercher();
            googleCalendarService.syncEventsToCalendar(allEvents);
            
            // Set up calendar view
            setupCalendarView();
            
            // Load upcoming events
            loadUpcomingEvents();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'initialisation du calendrier: " + e.getMessage());
        }
    }
    
    private void setupCalendarView() {
        // Add day headers
        String[] dayNames = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.getStyleClass().add("day-header");
            calendarGrid.add(dayLabel, i, 0);
        }
        
        updateCalendar();
    }
    
    private void updateCalendar() {
        // Clear existing calendar cells
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) > 0);
        
        // Update month label
        currentMonthLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " " + currentYearMonth.getYear());
        
        // Get the first day of the month
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        // Get the day of week (1-7, where 1 is Monday)
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        // Adjust to start from Monday (0-6)
        dayOfWeek = dayOfWeek == 7 ? 0 : dayOfWeek - 1;
        
        // Fill in the days
        for (int i = 1; i <= currentYearMonth.lengthOfMonth(); i++) {
            LocalDate date = currentYearMonth.atDay(i);
            VBox dayCell = createDayCell(date);
            
            // Calculate grid position
            int row = (i + dayOfWeek - 1) / 7 + 1;
            int col = (i + dayOfWeek - 1) % 7;
            
            calendarGrid.add(dayCell, col, row);
        }
    }
    
    private VBox createDayCell(LocalDate date) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("day-cell");
        cell.setPadding(new Insets(5));
        
        // Add day number
        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        cell.getChildren().add(dayLabel);
        
        // Check if there are events on this day
        List<Evenement> eventsOnDay = allEvents.stream()
            .filter(event -> event.getDate_debut().equals(date))
            .collect(Collectors.toList());
        
        if (!eventsOnDay.isEmpty()) {
            cell.getStyleClass().add("has-event");
            
            // Add event indicators
            for (Evenement event : eventsOnDay) {
                Label eventLabel = new Label(event.getTitle());
                eventLabel.getStyleClass().add("event-title");
                eventLabel.setWrapText(true);
                cell.getChildren().add(eventLabel);
            }
        }
        
        // Add click handler
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            showEventsForDate(date);
            // Update cell styles
            calendarGrid.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .forEach(node -> node.getStyleClass().remove("selected"));
            cell.getStyleClass().add("selected");
        });
        
        // Highlight today
        if (date.equals(LocalDate.now())) {
            cell.getStyleClass().add("today");
        }
        
        return cell;
    }
    
    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }
    
    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }
    
    @FXML
    private void handleToday() {
        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        updateCalendar();
        showEventsForDate(selectedDate);
    }
    
    private void loadUpcomingEvents() {
        LocalDate today = LocalDate.now();
        
        upcomingEventsContainer.getChildren().clear();
        
        allEvents.stream()
            .filter(event -> !event.getDate_debut().isBefore(today))
            .sorted((e1, e2) -> e1.getDate_debut().compareTo(e2.getDate_debut()))
            .forEach(event -> {
                VBox eventBox = createEventBox(event);
                upcomingEventsContainer.getChildren().add(eventBox);
            });
    }
    
    private void showEventsForDate(LocalDate date) {
        upcomingEventsContainer.getChildren().clear();
        
        allEvents.stream()
            .filter(event -> event.getDate_debut().equals(date))
            .forEach(event -> {
                VBox eventBox = createEventBox(event);
                upcomingEventsContainer.getChildren().add(eventBox);
            });
    }
    
    private VBox createEventBox(Evenement event) {
        VBox box = new VBox(5);
        box.getStyleClass().add("event-box");
        
        Label titleLabel = new Label(event.getTitle());
        titleLabel.getStyleClass().add("event-title");
        
        Label dateLabel = new Label("Date: " + event.getDate_debut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateLabel.getStyleClass().add("event-details");
        
        Label locationLabel = new Label("Lieu: " + event.getLatitude() + ", " + event.getLongitude());
        locationLabel.getStyleClass().add("event-details");
        
        box.getChildren().addAll(titleLabel, dateLabel, locationLabel);
        return box;
    }
    
    private void showError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setTextFill(Color.RED);
        upcomingEventsContainer.getChildren().add(errorLabel);
    }

    @FXML
    private void handleBack() {
        try {
            // Load the Navigation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Navigation.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set it up
            NavigationController controller = loader.getController();
            controller.setAdminMode(false);
            if (currentClient != null) {
                controller.setCurrentUser(currentClient);
            }
            
            // Get the current stage
            Stage stage = (Stage) calendarGrid.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("EventHub");
            stage.show();
            
            // Load the events view in the navigation
            controller.handleEvenements();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du retour à la vue principale: " + e.getMessage());
        }
    }
} 