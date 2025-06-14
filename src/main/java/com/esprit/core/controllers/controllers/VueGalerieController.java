package com.esprit.core.controllers.controllers;

import com.esprit.models.Espace;
import com.esprit.services.EspaceService;
import com.esprit.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VueGalerieController implements Initializable {

    @FXML
    private ComboBox<String> comboTypeFilter;
    
    @FXML
    private TextField txtMinCapacity;
    
    @FXML
    private TextField txtMaxCapacity;
    
    @FXML
    private CheckBox checkAvailableOnly;
    
    @FXML
    private TextField txtSearch;
    
    @FXML
    private Button btnApplyFilters;
    
    @FXML
    private Button btnResetFilters;
    
    @FXML
    private TilePane spacesGrid;
    
    @FXML
    private Label lblResultCount;
    
    private EspaceService espaceService = new EspaceService();
    private List<Espace> allSpaces;
    private List<Espace> filteredSpaces;
    
    // Sample placeholder images from public URLs for demo purposes
    private final String[] demoImageUrls = {
        "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1414124488080-0188dcbb8834?w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1517048676732-d65bc937f952?w=600&auto=format&fit=crop"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialisation de VueGalerieController");
        loadSpaces();
        
        Espace selected = SessionManager.getSelectedEspace();
        System.out.println("Espace sélectionné depuis SessionManager: " + selected);
        if (selected != null) {
            focusOnEspace(selected);
        }

        // Check if there's a selected space from Gallery
        Espace selectedEspace = SessionManager.getSelectedEspace();
        if (selectedEspace != null) {
            // Focus on the selected space
            focusOnEspace(selectedEspace);
            // Clear the selection after handling
            SessionManager.setSelectedEspace(null);
        }

        // Setup numeric validation for capacity fields
        setupNumericValidation();
        
        // Populate type filter
        populateTypeFilter();
        
        // Apply initial filters (available only is checked by default)
        applyFilters();
    }
    
    private void setupNumericValidation() {
        // Only allow numbers in capacity fields
        txtMinCapacity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtMinCapacity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        txtMaxCapacity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtMaxCapacity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    private void loadSpaces() {
        // Load spaces from service
        allSpaces = espaceService.getAll();
        
        // If no spaces in database (for prototype purposes), create dummy data
        if (allSpaces == null || allSpaces.isEmpty()) {
            allSpaces = createDummySpaces();
        }
    }
    
    private List<Espace> createDummySpaces() {
        // Create dummy spaces with URLs for images
        List<Espace> dummySpaces = List.of(
            new Espace(1, "Grande Salle", "Conférence", 200, "Tunis Centre", 1500.0, true),
            new Espace(2, "Jardin Event", "Extérieur", 150, "Gammarth", 2000.0, true),
            new Espace(3, "Studio Photo", "Studio", 20, "La Marsa", 500.0, false),
            new Espace(4, "Salle de Réunion A", "Réunion", 15, "Les Berges du Lac", 350.0, true),
            new Espace(5, "Salle de Réunion B", "Réunion", 10, "Les Berges du Lac", 250.0, true),
            new Espace(6, "Salle de Conférence", "Conférence", 100, "Sousse", 1000.0, false),
            new Espace(7, "Espace Lounge", "Cocktail", 80, "Hammamet", 1200.0, true),
            new Espace(8, "Terrasse Vue Mer", "Extérieur", 100, "Hammamet", 1800.0, true)
        );
        
        // Assign demo images to each space
        for (int i = 0; i < dummySpaces.size(); i++) {
            dummySpaces.get(i).setImage(demoImageUrls[i % demoImageUrls.length]);
        }
        
        return dummySpaces;
    }
    
    private void populateTypeFilter() {
        // Get all unique types
        List<String> types = allSpaces.stream()
                .map(Espace::getType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // Add "All Types" option
        types.add(0, "Tous les Types");
        
        comboTypeFilter.setItems(FXCollections.observableArrayList(types));
        comboTypeFilter.getSelectionModel().selectFirst();
    }
    
    @FXML
    public void applyFilters() {
        // Apply filters
        filteredSpaces = allSpaces.stream()
                .filter(space -> {
                    // Filter by type
                    if (comboTypeFilter.getValue() != null && 
                        !comboTypeFilter.getValue().equals("Tous les Types") && 
                        !comboTypeFilter.getValue().equals(space.getType())) {
                        return false;
                    }
                    
                    // Filter by minimum capacity
                    if (!txtMinCapacity.getText().isEmpty()) {
                        int minCapacity = Integer.parseInt(txtMinCapacity.getText());
                        if (space.getCapacite() < minCapacity) {
                            return false;
                        }
                    }
                    
                    // Filter by maximum capacity
                    if (!txtMaxCapacity.getText().isEmpty()) {
                        int maxCapacity = Integer.parseInt(txtMaxCapacity.getText());
                        if (space.getCapacite() > maxCapacity) {
                            return false;
                        }
                    }
                    
                    // Filter by availability
                    if (checkAvailableOnly.isSelected() && !space.isDisponibilite()) {
                        return false;
                    }
                    
                    // Filter by search text (name or location)
                    if (!txtSearch.getText().isEmpty()) {
                        String searchText = txtSearch.getText().toLowerCase();
                        boolean nameMatches = space.getNom().toLowerCase().contains(searchText);
                        boolean locationMatches = space.getLocalisation().toLowerCase().contains(searchText);
                        
                        if (!nameMatches && !locationMatches) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // Update the UI with filtered spaces
        updateSpacesGrid();
    }
    
    @FXML
    public void resetFilters() {
        comboTypeFilter.getSelectionModel().selectFirst();
        txtMinCapacity.clear();
        txtMaxCapacity.clear();
        checkAvailableOnly.setSelected(true);
        txtSearch.clear();
        
        applyFilters();
    }
    
    private void updateSpacesGrid() {
        // Clear existing cards
        spacesGrid.getChildren().clear();
        
        // Update count label
        lblResultCount.setText("Affichage de " + filteredSpaces.size() + " espaces");
        
        // Create a card for each space
        for (int i = 0; i < filteredSpaces.size(); i++) {
            Espace space = filteredSpaces.get(i);
            
            // Create space card
            VBox spaceCard = createSpaceCard(space, i);
            
            // Add to grid
            spacesGrid.getChildren().add(spaceCard);
        }
    }
    
    private VBox createSpaceCard(Espace space, int index) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(280);
        card.setMinHeight(400);
        card.getStyleClass().add("space-card");
        
        // Background and border
        card.setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(
                Color.rgb(230, 230, 230), BorderStrokeStyle.SOLID, 
                new CornerRadii(5), new BorderWidths(1))));
        
        // Add drop shadow effect
        card.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        
        // Try to load the space's image from URL or file path
        try {
            if (space.getImage() != null && !space.getImage().isEmpty()) {
                String imagePath = space.getImage();
                
                // Determine if this is a URL or a file path
                if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                    // This is a URL - load directly
                    imageView.setImage(new Image(imagePath));
                } else {
                    // This might be a file path - check if it exists
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        // It's a local file that exists
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        // Try to load from resources
                        URL resourceUrl = getClass().getClassLoader().getResource(imagePath);
                        if (resourceUrl != null) {
                            imageView.setImage(new Image(resourceUrl.toExternalForm()));
                        } else {
                            // Fallback to demo images
                            imageView.setImage(new Image(demoImageUrls[index % demoImageUrls.length]));
                        }
                    }
                }
            } else {
                // Fallback to demo images if no image is set
                imageView.setImage(new Image(demoImageUrls[index % demoImageUrls.length]));
            }
        } catch (Exception e) {
            // Fallback to a placeholder
            System.out.println("Failed to load image for " + space.getNom() + ": " + e.getMessage());
            try {
                // Try to load default placeholder
                URL placeholderUrl = getClass().getClassLoader().getResource("images/spaces/default.jpg");
                if (placeholderUrl != null) {
                    imageView.setImage(new Image(placeholderUrl.toExternalForm()));
                } else {
                    // If placeholder not found, use a demo image
                    imageView.setImage(new Image(demoImageUrls[0]));
                }
            } catch (Exception ex) {
                // If even that fails, just use a colored background
                imageView.setStyle("-fx-background-color: #cccccc;");
            }
        }
        
        // Title
        Label titleLabel = new Label(space.getNom());
        titleLabel.setWrapText(true);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Type and Capacity
        Label typeLabel = new Label(space.getType() + " • Capacité: " + space.getCapacite());
        typeLabel.setTextFill(Color.GRAY);
        
        // Location
        Label locationLabel = new Label("📍 " + space.getLocalisation());
        
        // Price
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.FRENCH);
        Label priceLabel = new Label(currencyFormat.format(space.getPrix()) + " TND/jour");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        priceLabel.setTextFill(Color.rgb(52, 152, 219));
        
        // Availability
        Label availabilityLabel = new Label(space.isDisponibilite() ? "✅ Disponible" : "❌ Non Disponible");
        availabilityLabel.setTextFill(space.isDisponibilite() ? Color.rgb(46, 204, 113) : Color.rgb(231, 76, 60));
        availabilityLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        
        // Add a short description if available
        VBox descriptionBox = new VBox();
        if (space.getDescription() != null && !space.getDescription().isEmpty()) {
            Label descLabel = new Label(space.getDescription().length() > 60 
                ? space.getDescription().substring(0, 60) + "..." 
                : space.getDescription());
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");
            descriptionBox.getChildren().add(descLabel);
            descriptionBox.setPadding(new Insets(5, 0, 5, 0));
        }
        
        // Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        buttonContainer.setAlignment(Pos.CENTER);
        
        // Reserve button
        Button reserveButton = new Button("Réserver");
        reserveButton.getStyleClass().add("primary-button");
        reserveButton.setDisable(!space.isDisponibilite());
        reserveButton.setOnAction(event -> showReservationForm(space));
        
        // View Location button
        Button viewLocationButton = new Button("Voir sur la Carte");
        viewLocationButton.getStyleClass().add("secondary-button");
        viewLocationButton.setOnAction(event -> showLocationOnMap(space));
        
        buttonContainer.getChildren().addAll(reserveButton, viewLocationButton);
        
        // Add all elements to card
        card.getChildren().addAll(
                imageView, titleLabel, typeLabel, 
                locationLabel, priceLabel, availabilityLabel
        );
        
        // Add description if available
        if (!descriptionBox.getChildren().isEmpty()) {
            card.getChildren().add(descriptionBox);
        }
        
        card.getChildren().addAll(new Separator(), buttonContainer);
        
        setupSpaceSelection(card, space);
        
        return card;
    }
    
    private void setupSpaceSelection(VBox card, Espace espace) {
        card.setOnMouseClicked(event -> {
            try {
                // Charger le formulaire de réservation
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/FormulaireReservation.fxml"));
                Parent root = loader.load();
                
                // Passer les données de l'espace au contrôleur
                FormulaireReservationController controller = loader.getController();
                controller.setEspaceData(espace);
                
                // Afficher dans une nouvelle fenêtre ou dans le panneau principal
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Réservation - " + espace.getNom());
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement du formulaire de réservation", "Impossible d'ouvrir le formulaire de réservation");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showReservationForm(Espace space) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/FormulaireReservation.fxml"));
            Parent root = loader.load();
            
            // Get controller and pass the space
            FormulaireReservationController controller = loader.getController();
            controller.setEspaceData(space);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Réserver l'Espace");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
            // Refresh the gallery after reservation
            loadSpaces();
            applyFilters();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la réservation", "Impossible de charger le formulaire de réservation");
        }
    }
    
    private void showLocationOnMap(Espace space) {
        try {
            // Create a mini map view in a popup
            Stage mapStage = new Stage();
            mapStage.setTitle("Localisation: " + space.getLocalisation());
            mapStage.initModality(Modality.WINDOW_MODAL);
            mapStage.initOwner(spacesGrid.getScene().getWindow());
            
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            
            // Load the OSM map HTML - assurons-nous d'utiliser la méthode standard de chargement
            URL mapUrl = getClass().getClassLoader().getResource("views/osm_map.html");
            if (mapUrl == null) {
                throw new IOException("Fichier de carte introuvable: views/osm_map.html");
            }
            webEngine.load(mapUrl.toExternalForm());
            
            // Add a toolbar at the top for better user experience
            Button btnClose = new Button("Fermer");
            btnClose.setOnAction(e -> mapStage.close());
            
            Label lblLocation = new Label("Espace: " + space.getNom() + " à " + space.getLocalisation());
            lblLocation.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            HBox toolbar = new HBox(10);
            toolbar.setPadding(new Insets(10));
            toolbar.setAlignment(Pos.CENTER_LEFT);
            toolbar.getChildren().addAll(lblLocation, new Region(), btnClose);
            HBox.setHgrow(toolbar.getChildren().get(1), Priority.ALWAYS);
            
            // Center the map on this space when the page is loaded
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    // When map loads, set the marker
                    double lat = getLatitudeFromLocation(space.getLocalisation());
                    double lng = getLongitudeFromLocation(space.getLocalisation());
                    
                    // Corriger les paramètres pour correspondre à la fonction JavaScript dans osm_map.html
                    String script = String.format(
                            "addMarker(%f, %f, '%s', '%s', %d, '%s', %f, %d, %b);", 
                            lat, lng, 
                            space.getNom().replace("'", "\\'"), 
                            space.getType().replace("'", "\\'"), 
                            space.getCapacite(),
                            space.getLocalisation().replace("'", "\\'"), 
                            space.getPrix(),
                            space.getEspaceId(), // Use espaceId instead of getId
                            space.isDisponibilite());
                    
                    webEngine.executeScript(script);
                }
            });
            
            BorderPane mapLayout = new BorderPane();
            mapLayout.setTop(toolbar);
            mapLayout.setCenter(webView);
            
            Scene scene = new Scene(mapLayout, 800, 600);
            mapStage.setScene(scene);
            mapStage.show();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'affichage de la carte", "Impossible d'afficher la carte: " + e.getMessage());
        }
    }
    
    // Helper method to get latitude from location (simplified mock for demo)
    private double getLatitudeFromLocation(String location) {
        // In a real app, you would use a geocoding service here
        // For demo purposes, we'll just return hardcoded values based on the location name
        switch (location.toLowerCase()) {
            case "tunis centre": return 36.8065;
            case "gammarth": return 36.9184;
            case "la marsa": return 36.8858;
            case "les berges du lac": return 36.8315;
            case "sousse": return 35.8245;
            case "hammamet": return 36.4064;
            default: return 36.8065; // Default to Tunis
        }
    }
    
    // Helper method to get longitude from location (simplified mock for demo)
    private double getLongitudeFromLocation(String location) {
        // In a real app, you would use a geocoding service here
        // For demo purposes, we'll just return hardcoded values based on the location name
        switch (location.toLowerCase()) {
            case "tunis centre": return 10.1815;
            case "gammarth": return 10.2881;
            case "la marsa": return 10.3226;
            case "les berges du lac": return 10.2407;
            case "sousse": return 10.6346;
            case "hammamet": return 10.6141;
            default: return 10.1815; // Default to Tunis
        }
    }
    
    void focusOnEspace(Espace espace) {
        if (espace == null || spacesGrid == null) return;
        
        // Find and select the space in the grid
        for (Node node : spacesGrid.getChildren()) {
            if (node instanceof VBox card) {
                if (!card.getChildren().isEmpty() && card.getChildren().size() > 1
                        && card.getChildren().get(1) instanceof Label) {
                    Label nameLabel = (Label) card.getChildren().get(1);
                    if (nameLabel.getText() != null && nameLabel.getText().equals(espace.getNom())) {
                        // Highlight the selected space
                        card.setStyle("-fx-border-color: #007bff; -fx-border-width: 2px;");
                        
                        // Configurer le clic sur l'espace (mais ne pas déclencher)
                        setupSpaceSelection(card, espace);
                        
                        // Scroll to the space
                        ScrollPane scrollPane = (ScrollPane) spacesGrid.getParent().getParent();
                        double viewportHeight = scrollPane.getViewportBounds().getHeight();
                        double nodeY = card.getBoundsInParent().getMinY();
                        scrollPane.setVvalue(nodeY / spacesGrid.getHeight());
                        break;
                    }
                }
            }
        }
    }
}
