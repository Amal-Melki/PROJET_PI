package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MesReservationsController implements Initializable {

    @FXML
    private TableView<ReservationEspace> reservationsTable;

    @FXML
    private TableColumn<ReservationEspace, Integer> colId;

    @FXML
    private TableColumn<ReservationEspace, String> colSpace;

    @FXML
    private TableColumn<ReservationEspace, LocalDate> colStartDate;

    @FXML
    private TableColumn<ReservationEspace, LocalDate> colEndDate;

    @FXML
    private TableColumn<ReservationEspace, Integer> colPeople;

    @FXML
    private TableColumn<ReservationEspace, String> colPhone;

    @FXML
    private TableColumn<ReservationEspace, String> colDescription;

    @FXML
    private TableColumn<ReservationEspace, String> colStatus;

    @FXML
    private TableColumn<ReservationEspace, ReservationEspace> colActions;

    @FXML
    private ComboBox<String> comboFilterStatus;

    @FXML
    private Button btnRefresh;

    @FXML
    private VBox emptyStateContainer;

    private ReservationEspaceService reservationService;
    private EspaceService espaceService;
    private ObservableList<ReservationEspace> reservationList;
    private String currentUser = "Utilisateur Invité"; // Would be set based on logged in user

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        reservationService = new ReservationEspaceService();
        espaceService = new EspaceService();

        // Setup table columns
        setupTableColumns();

        // Setup filters
        setupFilters();

        // Load reservations
        loadReservations();
    }

    private void loadReservations() {
        // Initialize the observable list if not already done
        if (reservationList == null) {
            reservationList = FXCollections.observableArrayList();
        }

        // Set the list to the table
        reservationsTable.setItems(reservationList);

        // Get reservations from the service
        List<ReservationEspace> reservations = reservationService.getAll();

        // If no reservations or service not working, create dummy data for testing
        if (reservations == null || reservations.isEmpty()) {
            reservations = createDummyReservations();
        }

        // Update the observable list
        reservationList.setAll(reservations);

        // Show empty state if no reservations
        if (reservationList.isEmpty() && emptyStateContainer != null) {
            emptyStateContainer.setVisible(true);
            reservationsTable.setVisible(false);
        } else {
            if (emptyStateContainer != null) {
                emptyStateContainer.setVisible(false);
            }
            reservationsTable.setVisible(true);
        }
    }

    private List<ReservationEspace> createDummyReservations() {
        // Get spaces (or create dummy spaces if needed)
        List<Espace> spaces = espaceService.getAll();
        if (spaces == null || spaces.isEmpty()) {
            spaces = createDummySpaces();
        }

        List<ReservationEspace> dummyReservations = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Past reservation
        ReservationEspace past = new ReservationEspace();
        past.setEspaceId(spaces.get(0).getId());
        past.setNomClient(currentUser);
        past.setEmailClient("MELKI.Amal@esprit.tn");
        past.setTelephoneClient("+216 71 123 456");
        past.setDateDebut(today.minusDays(30));
        past.setDateFin(today.minusDays(28));
        past.setNombrePersonnes(50);
        past.setDescription("Événement d'entreprise passé");
        past.setStatut("Terminée");
        past.setReservationId(1);
        dummyReservations.add(past);

        // Current reservation
        ReservationEspace current = new ReservationEspace();
        current.setEspaceId(spaces.get(1).getId());
        current.setNomClient(currentUser);
        current.setEmailClient("client@example.com");
        current.setTelephoneClient("+216 71 789 123");
        current.setDateDebut(today.minusDays(1));
        current.setDateFin(today.plusDays(2));
        current.setNombrePersonnes(150);
        current.setDescription("Mariage en cours");
        current.setStatut("En cours");
        current.setReservationId(2);
        dummyReservations.add(current);

        // Upcoming reservation
        ReservationEspace upcoming = new ReservationEspace();
        upcoming.setEspaceId(spaces.get(2).getId());
        upcoming.setNomClient(currentUser);
        upcoming.setEmailClient("client@example.com");
        upcoming.setTelephoneClient("+216 71 456 789");
        upcoming.setDateDebut(today.plusDays(15));
        upcoming.setDateFin(today.plusDays(17));
        upcoming.setNombrePersonnes(20);
        upcoming.setDescription("Séance photo à venir");
        upcoming.setStatut("Confirmée");
        upcoming.setReservationId(3);
        dummyReservations.add(upcoming);

        return dummyReservations;
    }

    private List<Espace> createDummySpaces() {
        // Same dummy data as in other controllers for consistency
        List<Espace> dummySpaces = new ArrayList<>();

        dummySpaces.add(new Espace(1, "Grande Salle", "Conférence", 200, "Tunis Centre", 1500.0, true));
        dummySpaces.add(new Espace(2, "Jardin Event", "Extérieur", 150, "Gammarth", 2000.0, true));
        dummySpaces.add(new Espace(3, "Studio Photo", "Studio", 20, "La Marsa", 500.0, false));

        return dummySpaces;
    }

    private void setupTableColumns() {
        // Basic columns
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getReservationId()));

        // Space name (requires a lookup to the EspaceService)
        colSpace.setCellValueFactory(data -> {
            int espaceId = data.getValue().getEspaceId();
            Espace espace = espaceService.getById(espaceId);
            return new SimpleStringProperty(espace != null ? espace.getNom() : "Espace Inconnu");
        });

        // Date columns
        colStartDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDateDebut()));
        colStartDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });

        colEndDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDateFin()));
        colEndDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });

        // Number of people
        colPeople.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getNombrePersonnes()));

        // Phone number
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelephoneClient()));

        // Description (truncated if too long)
        colDescription.setCellValueFactory(data -> {
            String description = data.getValue().getDescription();
            if (description != null && description.length() > 30) {
                description = description.substring(0, 27) + "...";
            }
            return new SimpleStringProperty(description);
        });

        // Status column (now from the database)
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut()));

        // Add custom cell factory for status to show colored badges
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(status);
                    setAlignment(Pos.CENTER);

                    // Set different background colors based on status
                    switch (status) {
                        case "En cours":
                            setStyle("-fx-background-color: #f8bbd0; -fx-text-fill: #c2185b; -fx-font-weight: bold; -fx-padding: 2px 8px; -fx-background-radius: 4px;");
                            break;
                        case "En attente":
                            setStyle("-fx-background-color: #fce4ec; -fx-text-fill: #d81b60; -fx-font-weight: bold; -fx-padding: 2px 8px; -fx-background-radius: 4px;");
                            break;
                        case "Confirmée":
                            setStyle("-fx-background-color: #e1bee7; -fx-text-fill: #8e24aa; -fx-font-weight: bold; -fx-padding: 2px 8px; -fx-background-radius: 4px;");
                            break;
                        case "Annulée":
                            setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-padding: 2px 8px; -fx-background-radius: 4px;");
                            break;
                        case "Terminée":
                            setStyle("-fx-background-color: #ede7f6; -fx-text-fill: #5e35b1; -fx-font-weight: bold; -fx-padding: 2px 8px; -fx-background-radius: 4px;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Action buttons column
        colActions.setCellFactory(createButtonCellFactory());
    }

    private void setupFilters() {
        // Setup status filter options
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "Toutes les Réservations", "À venir", "En cours", "Passées"
        );
        comboFilterStatus.setItems(statusOptions);
        comboFilterStatus.getSelectionModel().selectFirst();

        // Add listener for filtering
        comboFilterStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterReservationsByStatus(newVal);
            }
        });
    }

    private void filterReservationsByStatus(String status) {
        // Start with all reservations
        List<ReservationEspace> allReservations = reservationService.getAll();
        if (allReservations == null || allReservations.isEmpty()) {
            allReservations = createDummyReservations();
        }

        // If "All Reservations" is selected, show everything
        if (status.equals("Toutes les Réservations")) {
            reservationList.setAll(allReservations);
            return;
        }

        // Otherwise, filter by the selected status
        LocalDate today = LocalDate.now();
        List<ReservationEspace> filtered = allReservations.stream()
                .filter(r -> {
                    LocalDate startDate = r.getDateDebut();
                    LocalDate endDate = r.getDateFin();

                    switch (status) {
                        case "À venir":
                            return today.isBefore(startDate);
                        case "En cours":
                            return !today.isBefore(startDate) && !today.isAfter(endDate);
                        case "Passées":
                            return today.isAfter(endDate);
                        default:
                            return true;
                    }
                })
                .toList();

        reservationList.setAll(filtered);
    }

    @FXML
    private void refreshReservations() {
        loadReservations();
    }

    private void showReservationDetails(ReservationEspace reservation) throws IOException {
        // Get space info
        Espace space = espaceService.getById(reservation.getEspaceId());
        if (space == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Espace Non Trouvé",
                    "Impossible de trouver l'espace associé à cette réservation.");
            return;
        }

        // Generate PDF with all details
        File pdfFile = generateReservationPDF(reservation, space);

        // Open the PDF file
        Desktop.getDesktop().open(pdfFile);
    }

    private void confirmCancelReservation(ReservationEspace reservation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer l'Annulation");
        confirmDialog.setHeaderText("Annuler la Réservation #" + reservation.getReservationId());
        confirmDialog.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ? Cette action ne peut pas être annulée.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Proceed with cancellation
            try {
                // Delete reservation
                reservationService.delete(reservation.getReservationId());

                // Update space availability
                Espace space = espaceService.getById(reservation.getEspaceId());
                if (space != null) {
                    space.setDisponibilite(true);
                    espaceService.update(space);
                }

                // Refresh the list
                refreshReservations();

                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation Annulée",
                        "Votre réservation a été annulée avec succès.");

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'Annulation",
                        "Impossible d'annuler la réservation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private File generateReservationPDF(ReservationEspace reservation, Espace space) throws IOException {
        // Create a temporary file to store the PDF
        File tempFile = File.createTempFile("reservation_" + reservation.getReservationId(), ".pdf");

        // Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Create a content stream for writing to the page
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Set up fonts
            PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
            PDType1Font subtitleFont = PDType1Font.HELVETICA_BOLD;
            PDType1Font normalFont = PDType1Font.HELVETICA;

            // Starting position (top of page)
            float y = page.getMediaBox().getHeight() - 50;
            float margin = 50;

            // Add title
            contentStream.beginText();
            contentStream.setFont(titleFont, 20);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("EVENCIA PLANIFICATEUR D'ÉVÉNEMENTS");
            contentStream.endText();

            y -= 30;

            // Add subtitle
            contentStream.beginText();
            contentStream.setFont(subtitleFont, 16);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Détails de la Réservation - #" + reservation.getReservationId());
            contentStream.endText();

            y -= 40;

            // Space information
            contentStream.beginText();
            contentStream.setFont(subtitleFont, 14);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Informations sur l'Espace");
            contentStream.endText();

            y -= 20;

            String[] spaceDetails = {
                    "Nom : " + space.getNom(),
                    "Type : " + space.getType(),
                    "Capacité : " + space.getCapacite() + " personnes",
                    "Lieu : " + space.getLocalisation(),
                    "Prix par jour : " + currencyFormat.format(space.getPrix())
            };

            for (String detail : spaceDetails) {
                y -= 20;
                contentStream.beginText();
                contentStream.setFont(normalFont, 12);
                contentStream.newLineAtOffset(margin + 20, y);
                contentStream.showText(detail);
                contentStream.endText();
            }

            y -= 40;

            // Reservation details
            contentStream.beginText();
            contentStream.setFont(subtitleFont, 14);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Détails de la Réservation");
            contentStream.endText();

            y -= 20;

            // Calculate duration and total cost
            long days = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
            double totalCost = days * space.getPrix();

            String[] reservationDetails = {
                    "Client : " + reservation.getNomClient(),
                    "Date de début : " + reservation.getDateDebut().format(dateFormatter),
                    "Date de fin : " + reservation.getDateFin().format(dateFormatter),
                    "Durée : " + days + " jour" + (days > 1 ? "s" : ""),
                    "Coût total : " + currencyFormat.format(totalCost)
            };

            for (String detail : reservationDetails) {
                y -= 20;
                contentStream.beginText();
                contentStream.setFont(normalFont, 12);
                contentStream.newLineAtOffset(margin + 20, y);
                contentStream.showText(detail);
                contentStream.endText();
            }

            y -= 40;

            // Status information
            String status = reservation.getStatut();
            String statusDisplay = status;
            LocalDate today = LocalDate.now();
            if (status.equals("En attente") || status.equals("Confirmée")) {
                if (today.isBefore(reservation.getDateDebut())) {
                    statusDisplay = status + " (À venir)";
                } else if (today.isAfter(reservation.getDateFin())) {
                    statusDisplay = status + " (Passée)";
                } else {
                    statusDisplay = status + " (En cours)";
                }
            }

            contentStream.beginText();
            contentStream.setFont(subtitleFont, 14);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Statut : " + statusDisplay);
            contentStream.endText();

            y -= 60;

            // Footer
            contentStream.beginText();
            contentStream.setFont(normalFont, 10);
            contentStream.newLineAtOffset(margin, 50);
            contentStream.showText("Ce document a été généré le " + LocalDate.now().format(dateFormatter));
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(normalFont, 10);
            contentStream.newLineAtOffset(margin, 35);
            contentStream.showText("Pour toute question, veuillez contacter Evencia Planificateur d'Événements à support@evencia.com");
            contentStream.endText();
        }

        // Save the document
        document.save(tempFile);
        document.close();

        return tempFile;
    }

    @FXML
    private void showGalleryView() {
        // This would be handled by the parent dashboard controller
        // Since we're in a tab-based layout, we can't directly switch tabs from here
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Galerie des Espaces",
                "Cliquez sur le bouton Galerie des Espaces dans la barre latérale pour parcourir les espaces disponibles.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Callback<TableColumn<ReservationEspace, ReservationEspace>, TableCell<ReservationEspace, ReservationEspace>> createButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button btnViewDetails = new Button("Détails");
            private final Button btnCancel = new Button("Annuler");
            private final HBox pane = new HBox(5, btnViewDetails, btnCancel);

            {
                // Style buttons
                btnViewDetails.getStyleClass().add("secondary-button");
                btnCancel.getStyleClass().add("delete-button");
                pane.setAlignment(Pos.CENTER);

                // Configure view details button
                btnViewDetails.setOnAction(event -> {
                    ReservationEspace reservation = getTableRow().getItem();
                    if (reservation != null) {
                        try {
                            showReservationDetails(reservation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Configure cancel button
                btnCancel.setOnAction(event -> {
                    ReservationEspace reservation = getTableRow().getItem();
                    if (reservation != null) {
                        confirmCancelReservation(reservation);
                    }
                });
            }

            @Override
            protected void updateItem(ReservationEspace reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if (empty || reservation == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(pane);

                // Disable cancel button for past reservations
                LocalDate now = LocalDate.now();
                LocalDate startDate = reservation.getDateDebut();
                boolean isPastReservation = now.isAfter(startDate);

                btnCancel.setDisable(isPastReservation);
                if (isPastReservation) {
                    btnCancel.setTooltip(new Tooltip("Impossible d'annuler les réservations passées"));
                } else {
                    btnCancel.setTooltip(null);
                }
            }
        };
    }
}
