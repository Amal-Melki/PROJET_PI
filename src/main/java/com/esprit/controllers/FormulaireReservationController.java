package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import com.esprit.services.EmailService;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class FormulaireReservationController implements Initializable {

    @FXML
    private Label lblSpaceName;

    @FXML
    private Label lblSpaceType;

    @FXML
    private Label lblSpaceCapacity;

    @FXML
    private Label lblSpaceLocation;

    @FXML
    private Label lblSpacePrice;

    @FXML
    private TextField txtClientName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private TextField txtNumberOfPeople;

    @FXML
    private TextArea txtDescription;

    @FXML
    private VBox summaryBox;

    @FXML
    private Label lblDuration;

    @FXML
    private Label lblTotalCost;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnPreviewPDF;

    @FXML
    private Button btnReserve;

    private Espace selectedSpace;
    private EspaceService espaceService = new EspaceService();
    private ReservationEspaceService reservationService = new ReservationEspaceService();
    private EmailService emailService = new EmailService();

    // Number format for currency
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Controller initialized"); // Debug log

        // Setup date picker with constraints (e.g., no past dates)
        setupDatePickers();

        // Setup numeric validation for number of people field
        setupNumericFieldValidation();

        // Style the buttons to make them more visible
        styleButtons();

        // Update summary when dates change
        if (dateStart != null && dateEnd != null) {
            dateStart.valueProperty().addListener((obs, oldVal, newVal) -> updateSummary());
            dateEnd.valueProperty().addListener((obs, oldVal, newVal) -> updateSummary());
        }

        // Add debug listeners to buttons to verify they're working
        if (btnCancel != null) {
            btnCancel.setOnAction(e -> {
                System.out.println("Cancel button clicked");
                handleCancel();
            });
        }

        if (btnPreviewPDF != null) {
            btnPreviewPDF.setOnAction(e -> {
                System.out.println("Preview PDF button clicked");
                handlePreviewPDF();
            });
        }

        if (btnReserve != null) {
            btnReserve.setOnAction(e -> {
                System.out.println("Reserve button clicked");
                handleReserve();
            });
        }
    }

    /**
     * Set the space that is being reserved
     * @param space The space to reserve
     */
    public void setSpace(Espace space) {
        this.selectedSpace = space;

        if (space != null) {
            // Populate space info
            if (lblSpaceName != null) lblSpaceName.setText(space.getNom());
            if (lblSpaceType != null) lblSpaceType.setText(space.getType());
            if (lblSpaceCapacity != null) lblSpaceCapacity.setText(String.valueOf(space.getCapacite()) + " personnes");
            if (lblSpaceLocation != null) lblSpaceLocation.setText(space.getLocalisation());
            if (lblSpacePrice != null) lblSpacePrice.setText(currencyFormat.format(space.getPrix()) + "/jour");

            // Initialize the start date to tomorrow
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            if (dateStart != null) dateStart.setValue(tomorrow);

            // Initialize the end date to the day after tomorrow
            if (dateEnd != null) dateEnd.setValue(tomorrow.plusDays(1));

            // Update the summary
            updateSummary();
        }
    }

    private void setupDatePickers() {
        if (dateStart != null) {
            // Ensure start date is not in the past
            dateStart.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });
        }

        if (dateEnd != null) {
            // Ensure end date is after start date
            dateEnd.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (dateStart != null && dateStart.getValue() != null) {
                        setDisable(empty || date.isBefore(dateStart.getValue()));
                    } else {
                        setDisable(empty || date.isBefore(LocalDate.now()));
                    }
                }
            });
        }
    }

    private void setupNumericFieldValidation() {
        if (txtNumberOfPeople != null) {
            // Allow only numeric input for the number of people field
            txtNumberOfPeople.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    // If the new value is not a number, revert to old value
                    if (!newValue.matches("\\d*")) {
                        txtNumberOfPeople.setText(oldValue);
                    }
                }
            });

            // Add validation when field loses focus
            txtNumberOfPeople.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) { // Focus lost
                    validateNumberOfPeople();
                }
            });
        }
    }

    private boolean validateNumberOfPeople() {
        try {
            Integer.parseInt(txtNumberOfPeople.getText().trim());
            return true;
        } catch (NumberFormatException e) {
            showErrorAlert("Nombre de personnes invalide");
            return false;
        }
    }

    private void styleButtons() {
        if (btnCancel != null) btnCancel.getStyleClass().add("btn-cancel");
        if (btnPreviewPDF != null) btnPreviewPDF.getStyleClass().add("btn-preview");
        if (btnReserve != null) btnReserve.getStyleClass().add("btn-primary");
    }

    private void updateSummary() {
        if (dateStart != null && dateEnd != null && dateStart.getValue() != null &&
                dateEnd.getValue() != null && selectedSpace != null) {

            // Calculate duration in days
            long days = ChronoUnit.DAYS.between(dateStart.getValue(), dateEnd.getValue()) + 1; // +1 to include end date

            // Update duration label
            if (lblDuration != null) {
                lblDuration.setText(days + " jour" + (days > 1 ? "s" : ""));
            }

            // Calculate total cost
            double totalCost = days * selectedSpace.getPrix();

            // Update total cost label
            if (lblTotalCost != null) {
                lblTotalCost.setText(currencyFormat.format(totalCost));
            }
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("handleCancel method called"); // Debug log

        try {
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Annuler la Réservation");
            alert.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Check if we are in a dialog or main screen
                if (btnCancel != null && btnCancel.getScene() != null && btnCancel.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) btnCancel.getScene().getWindow();
                    if (stage.getModality() == Modality.WINDOW_MODAL) {
                        // If it's a modal dialog, simply close it
                        closeDialog();
                    } else {
                        // If it's a main window, return to space consultation screen
                        retourConsulterEspaces();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Une erreur s'est produite: " + e.getMessage());
        }
    }

    /**
     * Return to space consultation screen
     */
    private void retourConsulterEspaces() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/VueGalerie.fxml"));
            Parent root = loader.load();
            if (btnCancel != null && btnCancel.getScene() != null) {
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", null,
                    "Impossible de retourner à l'écran de consultation des espaces: " + e.getMessage());
        }
    }

    @FXML
    private void handlePreviewPDF() {
        System.out.println("handlePreviewPDF method called"); // Debug log

        try {
            // Validate form fields first
            if (!validateForm(false)) {
                System.out.println("Form validation failed");
                return;
            }

            // Generate a temporary PDF file for preview
            String tempIdentifier = "preview_" + System.currentTimeMillis();
            File pdfFile = generateReservationPDF(tempIdentifier);

            // Check if Desktop is supported
            if (Desktop.isDesktopSupported()) {
                // Open the PDF file for preview
                Desktop.getDesktop().open(pdfFile);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "PDF Généré", null,
                        "Le PDF a été généré: " + pdfFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher l'aperçu",
                    "Une erreur s'est produite lors de la génération de l'aperçu : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    @FXML
    private void handleReserve() {
        try {
            // Validate form
            if (!validateForm(true) || !validateNumberOfPeople()) {
                return;
            }

            // Create reservation object
            ReservationEspace reservation = new ReservationEspace();
            reservation.setEspace(selectedSpace);
            reservation.setNomClient(txtClientName.getText().trim());
            reservation.setEmail(txtEmail.getText().trim());
            reservation.setTelephone(txtPhone.getText().trim());
            reservation.setDateDebut(dateStart.getValue());
            reservation.setDateFin(dateEnd.getValue());
            reservation.setNombrePersonnes(Integer.parseInt(txtNumberOfPeople.getText().trim()));
            reservation.setDescription(txtDescription.getText().trim());
            reservation.setStatus("En attente");
            
            // Calculate price
            long days = ChronoUnit.DAYS.between(dateStart.getValue(), dateEnd.getValue()) + 1;
            reservation.setPrixTotal(days * selectedSpace.getPrix());

            // Call service to add reservation
            int reservationId = reservationService.addReservation(reservation);
            
            if (reservationId == -1) {
                showErrorAlert("Échec de l'enregistrement");
                return;
            }

            // Generate and send confirmation
            File pdfFile = generateReservationPDF("reservation_" + reservationId);
            emailService.sendEmailWithAttachment(
                txtEmail.getText().trim(),
                "Confirmation Réservation #" + reservationId,
                "Détails de votre réservation...",
                pdfFile
            );

            showSuccessAlert("Réservation #" + reservationId + " confirmée!");
            closeDialog();
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur: " + e.getMessage());
        }
    }

    private boolean validateForm(boolean fullValidation) {
        boolean isValid = true;

        // Validate client name
        if (txtClientName != null) {
            if (txtClientName.getText().trim().isEmpty()) {
                txtClientName.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                txtClientName.setStyle("");
            }
        }

        // Validate email
        if (fullValidation && txtEmail != null) {
            if (!isValidEmail(txtEmail.getText().trim())) {
                txtEmail.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                txtEmail.setStyle("");
            }
        }

        // Validate phone
        if (fullValidation && txtPhone != null) {
            if (!isValidPhone(txtPhone.getText().trim())) {
                txtPhone.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                txtPhone.setStyle("");
            }
        }

        // Validate dates
        if (dateStart != null) {
            if (dateStart.getValue() == null) {
                dateStart.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                dateStart.setStyle("");
            }
        }

        if (dateEnd != null) {
            if (dateEnd.getValue() == null) {
                dateEnd.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                dateEnd.setStyle("");
            }
        }

        if (!isValid && fullValidation) {
            showAlert(Alert.AlertType.WARNING, "Formulaire Incomplet", null,
                    "Veuillez remplir tous les champs obligatoires correctement.");
        }

        return isValid;
    }

    private boolean isValidPhone(String phone) {
        // Simple validation: must be at least 8 digits
        return phone != null && phone.matches("\\d{8,}");
    }

    private boolean isValidEmail(String email) {
        // Simple email validation regex
        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private File generateReservationPDF(String identifier) throws IOException {
        // Create a temporary file to store the PDF
        File file = File.createTempFile(identifier, ".pdf");

        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Text encoding fixes for French accents and special characters
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set up basic formatting
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Confirmation de Reservation");
                contentStream.endText();

                // Add reservation details using standard ASCII characters only
                float yPosition = 700;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Details of the space
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Details de l'Espace:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Space name
                if (selectedSpace != null) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Nom: " + selectedSpace.getNom());
                    contentStream.endText();

                    // Continue with other details...
                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Type: " + selectedSpace.getType());
                    contentStream.endText();

                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Localisation: " + selectedSpace.getLocalisation());
                    contentStream.endText();
                }

                // Add more details about the reservation here
                yPosition -= 40;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Details de la Reservation:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Client details
                if (txtClientName != null && !txtClientName.getText().trim().isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Client: " + txtClientName.getText().trim());
                    contentStream.endText();
                    yPosition -= 20;
                }

                if (txtEmail != null && !txtEmail.getText().trim().isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Email: " + txtEmail.getText().trim());
                    contentStream.endText();
                    yPosition -= 20;
                }

                if (txtPhone != null && !txtPhone.getText().trim().isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Telephone: " + txtPhone.getText().trim());
                    contentStream.endText();
                    yPosition -= 20;
                }

                if (dateStart != null && dateEnd != null &&
                        dateStart.getValue() != null && dateEnd.getValue() != null) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    contentStream.showText("Du: " + dateStart.getValue().format(formatter) +
                            " au " + dateEnd.getValue().format(formatter));
                    contentStream.endText();
                    yPosition -= 20;
                }

                if (txtNumberOfPeople != null && !txtNumberOfPeople.getText().trim().isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Nombre de personnes: " + txtNumberOfPeople.getText().trim());
                    contentStream.endText();
                    yPosition -= 20;
                }

                // Payment details
                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Details du Paiement:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                if (dateStart != null && dateEnd != null &&
                        dateStart.getValue() != null && dateEnd.getValue() != null && selectedSpace != null) {

                    long days = ChronoUnit.DAYS.between(dateStart.getValue(), dateEnd.getValue()) + 1;

                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Duree: " + days + " jour" + (days > 1 ? "s" : ""));
                    contentStream.endText();

                    yPosition -= 20;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("Prix par jour: " + String.format("%.2f TND", selectedSpace.getPrix()));
                    contentStream.endText();

                    yPosition -= 20;
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    double totalPrice = days * selectedSpace.getPrix();
                    contentStream.showText("Total: " + String.format("%.2f TND", totalPrice));
                    contentStream.endText();
                }

                // Footer with generated date and legal text
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 50);
                contentStream.showText("Document genere le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 30);
                contentStream.showText("Pour toute question concernant votre reservation, veuillez nous contacter.");
                contentStream.endText();
            }

            // Save the document to the file
            document.save(file);
        }

        return file;
    }

    private void simulateSendEmail(String clientName, String email, File attachment) {
        try {
            // Prepare email content with reservation details
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Bonjour ").append(clientName).append(",\n\n");
            emailContent.append("Votre réservation a été confirmée avec succès.\n\n");
            emailContent.append("Détails de la réservation:\n");

            if (selectedSpace != null) {
                emailContent.append("Espace: ").append(selectedSpace.getNom()).append("\n");
                emailContent.append("Type: ").append(selectedSpace.getType()).append("\n");
                emailContent.append("Localisation: ").append(selectedSpace.getLocalisation()).append("\n");
            }

            if (dateStart != null && dateEnd != null &&
                    dateStart.getValue() != null && dateEnd.getValue() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                emailContent.append("Dates: du ").append(dateStart.getValue().format(formatter));
                emailContent.append(" au ").append(dateEnd.getValue().format(formatter)).append("\n");

                long days = ChronoUnit.DAYS.between(dateStart.getValue(), dateEnd.getValue()) + 1;
                double totalPrice = (selectedSpace != null) ? days * selectedSpace.getPrix() : 0;

                emailContent.append("Durée: ").append(days).append(" jour").append(days > 1 ? "s" : "").append("\n");
                emailContent.append("Prix total: ").append(String.format("%.2f TND", totalPrice)).append("\n\n");
            }

            emailContent.append("Vous trouverez en pièce jointe le PDF de confirmation de votre réservation.\n\n");
            emailContent.append("Merci de votre confiance et à bientôt chez Evencia!\n\n");
            emailContent.append("L'équipe Evencia");

            // Use email service to send the message
            emailService.sendEmail(email, "Confirmation de votre réservation chez Evencia", emailContent.toString());

            System.out.println("Email envoyé à: " + email);
            System.out.println("Avec le PDF: " + attachment.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeDialog() {
        if (btnCancel != null && btnCancel.getScene() != null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void highlightError(Control control, String message) {
        control.setStyle("-fx-border-color: #ff0000; -fx-border-width: 1px;");
        Tooltip tooltip = new Tooltip(message);
        control.setTooltip(tooltip);
    }

    private void clearError(Control control) {
        control.setStyle("");
        control.setTooltip(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Helper method to create or update a label in the summary box
     * @param container The container to add the label to
     * @param id The ID to use for the label
     * @param text The text for the label
     */
    private void createOrUpdateLabel(VBox container, String id, String text) {
        if (container == null) return;

        // Check if the label already exists
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof Label && node.getId() != null && node.getId().equals(id)) {
                ((Label) node).setText(text);
                return;
            }
        }

        // If not, create a new label
        Label label = new Label(text);
        label.setId(id);
        container.getChildren().add(label);
    }
}