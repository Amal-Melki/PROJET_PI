package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.services.EspaceService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GestionEspacesController implements Initializable {

    @FXML
    private TableView<Espace> tblSpaces;
    
    @FXML
    private TableColumn<Espace, Integer> colId;
    
    @FXML
    private TableColumn<Espace, String> colName;
    
    @FXML
    private TableColumn<Espace, String> colType;
    
    @FXML
    private TableColumn<Espace, Integer> colCapacity;
    
    @FXML
    private TableColumn<Espace, String> colLocation;
    
    @FXML
    private TableColumn<Espace, Double> colPrice;
    
    @FXML
    private TableColumn<Espace, Boolean> colAvailability;
    
    @FXML
    private TableColumn<Espace, String> colImage;
    
    @FXML
    private TableColumn<Espace, String> colPhotoUrl;
    
    @FXML
    private TableColumn<Espace, Espace> colActions;
    
    @FXML
    private ComboBox<String> comboTypeFilter;
    
    @FXML
    private CheckBox checkAvailableOnly;
    
    @FXML
    private TextField txtSearch;
    
    @FXML
    private Button btnAddSpace;
    
    @FXML
    private Button btnRefresh;
    
    private EspaceService espaceService;
    private ObservableList<Espace> spacesList;
    private FilteredList<Espace> filteredSpaces;

    private static final String IMAGES_BASE_PATH = "/images/spaces/";
    private static final String IMAGES_DIR = System.getProperty("user.dir") + "\\src\\main\\resources\\images\\spaces\\";
    private static final String DEFAULT_IMAGE = "default-space.png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the service
        espaceService = new EspaceService();
        
        // Set up the table columns
        setupTableColumns();
        
        // Load all spaces
        loadSpaces();
        
        // Set up search/filtering
        setupFiltering();
        
        // Set up button actions
        btnRefresh.setOnAction(e -> refreshSpacesList());
    }
    
    private void setupTableColumns() {
        // Set up the columns
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEspaceId()).asObject());
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        colCapacity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacite()).asObject());
        colLocation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocalisation()));
        colPrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrix()));
        
        // Format the price column
        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f TND", price));
                }
            }
        });
        
        // Format the availability column with colored text
        colAvailability.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponibilite()));
        colAvailability.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                } else {
                    setText(available ? "Disponible" : "Réservé");
                    setStyle(available ? 
                            "-fx-text-fill: #2ecc71; -fx-font-weight: bold;" : 
                            "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
        
        // Set up the image column to display thumbnails with click to enlarge
        colImage.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImage()));
        colImage.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }
            
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(new Label("Aucune image"));
                    setOnMouseClicked(null);
                } else {
                    try {
                        javafx.scene.image.Image img;
                        
                        if (imagePath == null || imagePath.isEmpty()) {
                            // Charger l'image par défaut
                            File defaultFile = new File(IMAGES_DIR + DEFAULT_IMAGE);
                            img = new javafx.scene.image.Image(defaultFile.toURI().toString(), 80, 50, true, true);
                        } else if (imagePath.startsWith("http")) {
                            // Chargement depuis URL web
                            img = new javafx.scene.image.Image(imagePath, 80, 50, true, true, true);
                            img.errorProperty().addListener((obs, wasError, isNowError) -> {
                                if (isNowError) setGraphic(new Label("URL invalide"));
                            });
                        } else {
                            // Chargement depuis fichier local
                            String cleanPath = imagePath.replace("..", "").replace("/", "\\");
                            File file = new File(IMAGES_DIR + cleanPath);
                            
                            if (file.exists()) {
                                img = new javafx.scene.image.Image(file.toURI().toString(), 80, 50, true, true);
                            } else {
                                // Fallback sur l'image par défaut
                                File defaultFile = new File(IMAGES_DIR + DEFAULT_IMAGE);
                                img = new javafx.scene.image.Image(defaultFile.toURI().toString(), 80, 50, true, true);
                            }
                        }
                        
                        imageView.setImage(img);
                        setGraphic(imageView);
                        setOnMouseClicked(event -> showEnlargedImage(img));
                    } catch (Exception e) {
                        System.out.println("Erreur chargement image: " + e.getMessage());
                        // Fallback ultime
                        setGraphic(new Label("Espace"));
                    }
                }
            }
        });
        
        // Set up the photo URL column to display image thumbnail from internet URL with click to enlarge
        colPhotoUrl.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPhotoUrl())
        );
        colPhotoUrl.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isEmpty()) {
                    setGraphic(null);
                    setOnMouseClicked(null);
                } else {
                    try {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(url, true);
                        imageView.setImage(img);
                        setGraphic(imageView);
                        setOnMouseClicked(event -> {
                            javafx.scene.image.ImageView enlargedView = new javafx.scene.image.ImageView(img);
                            enlargedView.setPreserveRatio(true);
                            enlargedView.setFitWidth(600);
                            enlargedView.setFitHeight(400);

                            javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane(enlargedView);
                            javafx.scene.Scene scene = new javafx.scene.Scene(pane);
                            javafx.stage.Stage stage = new javafx.stage.Stage();
                            stage.setTitle("Image Agrandie");
                            stage.setScene(scene);
                            stage.show();
                        });
                    } catch (Exception e) {
                        setGraphic(null);
                        setOnMouseClicked(null);
                    }
                }
            }
        });
        
        
        // Set up the actions column with Edit and Delete buttons
        colActions.setCellFactory(createButtonCellFactory());
    }
    
    private Callback<TableColumn<Espace, Espace>, TableCell<Espace, Espace>> createButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                // Style buttons
                btnEdit.getStyleClass().add("edit-button");
                btnDelete.getStyleClass().add("delete-button");
                pane.setAlignment(Pos.CENTER);
                
                // Configure edit button action
                btnEdit.setOnAction(event -> {
                    Espace space = getTableRow().getItem();
                    if (space != null) {
                        showEditSpaceDialog(space);
                    }
                });
                
                // Configure delete button action
                btnDelete.setOnAction(event -> {
                    Espace space = getTableRow().getItem();
                    if (space != null) {
                        confirmAndDeleteSpace(space);
                    }
                });
            }

            @Override
            protected void updateItem(Espace espace, boolean empty) {
                super.updateItem(espace, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }
    
    private void loadSpaces() {
        List<Espace> spaces = espaceService.getAll();
        
        // If there is no data in the service, create some dummy spaces for the prototype
        if (spaces == null || spaces.isEmpty()) {
            spaces = createDummySpaces();
        }
        
        spacesList = FXCollections.observableArrayList(spaces);
        
        // Initialize the filtered list
        filteredSpaces = new FilteredList<>(spacesList, p -> true);
        
        // Bind the sorted/filtered list to the table
        SortedList<Espace> sortedData = new SortedList<>(filteredSpaces);
        sortedData.comparatorProperty().bind(tblSpaces.comparatorProperty());
        tblSpaces.setItems(sortedData);
        
        // Populate the type filter dropdown
        updateTypeFilterOptions();
    }
    
    private List<Espace> createDummySpaces() {
        List<Espace> dummySpaces = new ArrayList<>();
        
        // Add some dummy spaces for demonstration
        dummySpaces.add(new Espace("Grande Salle", "Conférence", 200, "Tunis Centre", 1500.0, true));
        dummySpaces.add(new Espace("Jardin Event", "Extérieur", 150, "Gammarth", 2000.0, true));
        dummySpaces.add(new Espace("Studio Photo", "Studio", 20, "La Marsa", 500.0, false));
        dummySpaces.add(new Espace("Salle de Réunion A", "Réunion", 15, "Les Berges du Lac", 350.0, true));
        dummySpaces.add(new Espace("Salle de Réunion B", "Réunion", 10, "Les Berges du Lac", 250.0, true));
        dummySpaces.add(new Espace("Salle de Conférence", "Conférence", 100, "Sousse", 1000.0, false));
        dummySpaces.add(new Espace("Espace Lounge", "Cocktail", 80, "Hammamet", 1200.0, true));
        dummySpaces.add(new Espace("Terrasse Vue Mer", "Extérieur", 100, "Hammamet", 1800.0, true));
        
        // Simulate IDs for the dummy data
        for (int i = 0; i < dummySpaces.size(); i++) {
            dummySpaces.get(i).setEspaceId(i + 1);
        }
        
        return dummySpaces;
    }
    
    private void updateTypeFilterOptions() {
        // Get all unique space types
        List<String> spaceTypes = spacesList.stream()
                .map(Espace::getType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // Add "All Types" option at the beginning
        spaceTypes.add(0, "Tous les Types");
        
        // Update the combo box
        comboTypeFilter.setItems(FXCollections.observableArrayList(spaceTypes));
        comboTypeFilter.getSelectionModel().selectFirst();
    }
    
    private void setupFiltering() {
        // Add listeners to the filters
        comboTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        checkAvailableOnly.selectedProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        
        // Add listener to the search field
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }
    
    private void applyFilters() {
        filteredSpaces.setPredicate(createFilterPredicate());
    }
    
    private Predicate<Espace> createFilterPredicate() {
        return space -> {
            boolean matchesType = true;
            boolean matchesAvailability = true;
            boolean matchesSearch = true;
            
            // Check type filter
            String selectedType = comboTypeFilter.getValue();
            if (selectedType != null && !selectedType.equals("Tous les Types")) {
                matchesType = space.getType().equals(selectedType);
            }
            
            // Check availability filter
            if (checkAvailableOnly.isSelected()) {
                matchesAvailability = space.isDisponibilite();
            }
            
            // Check search text
            String searchText = txtSearch.getText().toLowerCase();
            if (searchText != null && !searchText.isEmpty()) {
                boolean nameMatches = space.getNom().toLowerCase().contains(searchText);
                boolean locationMatches = space.getLocalisation().toLowerCase().contains(searchText);
                matchesSearch = nameMatches || locationMatches;
            }
            
            return matchesType && matchesAvailability && matchesSearch;
        };
    }
    
    @FXML
    private void showAddSpaceDialog() {
        System.out.println("DEBUG: Add Space button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/AjouterEspace.fxml"));
            Parent root = loader.load();
            
            AjouterEspaceController controller = loader.getController();
            controller.setParentController(this);
            controller.initData(false);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Nouvel Espace");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAddSpace.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du dialogue d'ajout: " + e.getMessage());
            e.printStackTrace();
            
            // Afficher une alerte
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire d'ajout", "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    private void showEditSpaceDialog(Espace space) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/AjouterEspace.fxml"));
            Parent root = loader.load();
            
            AjouterEspaceController controller = loader.getController();
            controller.setParentController(this);
            controller.setEspaceToEdit(space);
            controller.initData(true);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier un Espace");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tblSpaces.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du dialogue de modification: " + e.getMessage());
            e.printStackTrace();
            
            // Afficher une alerte
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de modification", "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    private void confirmAndDeleteSpace(Espace space) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Suppression");
        alert.setHeaderText("Supprimer l'espace " + space.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet espace? Cette action ne peut pas être annulée.");
        
        // Customize the buttons
        ButtonType buttonTypeYes = new ButtonType("Oui, Supprimer");
        ButtonType buttonTypeNo = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            // User confirmed, delete the space
            int success = espaceService.delete(space.getEspaceId());
            
            if (success > 0) {
                // Remove from the list and show success message
                spacesList.remove(space);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Espace supprimé", "L'espace a été supprimé avec succès.");
                
                // Refresh the type filter options
                updateTypeFilterOptions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression", "Impossible de supprimer l'espace. Réessayez plus tard.");
            }
        }
    }
    
    /**
     * Affiche une alerte avec les paramètres spécifiés
     */
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    
    // Method to be called by the form controller after add/edit operations
    public void handleSpaceSaved() {
        refreshSpacesList();
    }
    
    /**
     * Rafraîchit la liste des espaces depuis la base de données
     */
    public void refreshSpacesList() {
        System.out.println("DEBUG: Refresh button clicked");
        // Récupérer les espaces mis à jour depuis la base de données
        List<Espace> spaces = espaceService.getAll();
        
        // Mettre à jour la liste observable
        spacesList.clear();
        spacesList.addAll(spaces);
        
        // Réappliquer les filtres
        applyFilters();
        
        // Mettre à jour les options de filtre par type
        updateTypeFilterOptions();
        
        // Sélectionner la première ligne si disponible
        if (!tblSpaces.getItems().isEmpty()) {
            tblSpaces.getSelectionModel().selectFirst();
            tblSpaces.scrollTo(0);
        }
    }
    
    private void showEnlargedImage(javafx.scene.image.Image img) {
        javafx.scene.image.ImageView enlargedView = new javafx.scene.image.ImageView(img);
        enlargedView.setPreserveRatio(true);
        enlargedView.setFitWidth(600);
        enlargedView.setFitHeight(400);

        StackPane pane = new StackPane(enlargedView);
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("Image Agrandie");
        stage.setScene(scene);
        stage.show();
    }
}
