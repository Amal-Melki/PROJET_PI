package com.esprit.controllers;

import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TableauDeBordPrincipalController implements Initializable {

    @FXML
    private VBox sidebar;

    @FXML
    private Label lblUsername;
    
    @FXML
    private Label lblContentTitle;
    
    @FXML
    private Label lblStatus;
    
    @FXML
    private TextField searchField;

    @FXML
    private Button btnOverview;

    @FXML
    private Button btnManageSpaces;

    @FXML
    private Button btnViewMap;
    
    @FXML
    private Button btnReservations;
    
    @FXML
    private Button btnAnalytics;

    @FXML
    private Button btnGalleryView;

    @FXML
    private Button btnMyReservations;
    
    @FXML
    private Button btnSearch;
    
    @FXML
    private Button btnSettings;
    
    @FXML
    private Button btnNotifications;
    
    @FXML
    private Button btnHelp;

    @FXML
    private Button btnSettingsFooter;

    @FXML
    private BorderPane contentArea;
    
    @FXML
    private StackPane contentPane;

    private String currentUser = "Utilisateur Invité";
    private EspaceService espaceService = new EspaceService();
    private ReservationEspaceService reservationService = new ReservationEspaceService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set username
        lblUsername.setText(currentUser);
        
        // Initialize search field
        searchField.setPromptText("Rechercher un espace...");
        searchField.setOnAction(e -> performSearch());

        // Set default view
        showOverview();
    }

    @FXML
    private void showOverview() {
        // Highlight the selected button
        setActiveButton(btnOverview);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/TableauDeBord.fxml"));
            Parent overview = loader.load();
            setContent(overview);
            updateTitle("Tableau de Bord", "Aperçu des statistiques et des performances");
            setStatusMessage("Tableau de bord chargé avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }

    @FXML
    private void showManageSpaces() {
        // Highlight the selected button
        setActiveButton(btnManageSpaces);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/GestionEspaces.fxml"));
            Parent manageSpaces = loader.load();
            setContent(manageSpaces);
            updateTitle("Gestion des Espaces", "Gérer les espaces disponibles");
            setStatusMessage("Gestion des espaces chargée avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }

    @FXML
    private void showMapView() {
        // Highlight the selected button
        setActiveButton(btnViewMap);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/VueCarte.fxml"));
            Parent mapView = loader.load();
            setContent(mapView);
            updateTitle("Carte des Espaces", "Visualiser la localisation des espaces");
            setStatusMessage("Carte chargée avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }
    
    @FXML
    private void showReservations() {
        // Highlight the selected button
        setActiveButton(btnReservations);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/GestionReservations.fxml"));
            Parent reservationsView = loader.load();
            setContent(reservationsView);
            updateTitle("Gestion des Réservations", "Gérer les réservations d'espaces");
            setStatusMessage("Gestion des réservations chargée avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }
    
    @FXML
    private void showAnalytics() {
        // Highlight the selected button
        setActiveButton(btnAnalytics);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/Analyses.fxml"));
            Parent analyticsView = loader.load();
            setContent(analyticsView);
            updateTitle("Analyses et Statistiques", "Aperçu détaillé des performances");
            setStatusMessage("Analyses chargées avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }

    @FXML
    private void showGalleryView() {
        // Highlight the selected button
        setActiveButton(btnGalleryView);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/VueGalerie.fxml"));
            Parent galleryView = loader.load();
            setContent(galleryView);
            updateTitle("Galerie d'Espaces", "Explorer les espaces disponibles");
            setStatusMessage("Galerie chargée avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }

    @FXML
    public void showMyReservations() {
        setActiveButton(btnMyReservations);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MesReservations.fxml"));
            Parent reservationsView = loader.load();
            setContent(reservationsView);
            updateTitle("Mes Réservations", "Gérer vos réservations personnelles");
            setStatusMessage("Mes réservations chargées avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }
    
    @FXML
    private void showSearch() {
        try {
            Parent searchView = FXMLLoader.load(getClass().getClassLoader().getResource("views/Recherche.fxml"));
            contentPane.getChildren().setAll(searchView);
            lblContentTitle.setText("Recherche Avancée");
            lblStatus.setText("Recherche chargée avec succès");
        } catch (IOException e) {
            System.err.println("Erreur de navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showSettings() {
        try {
            Parent settingsView = FXMLLoader.load(getClass().getClassLoader().getResource("views/Parametres.fxml"));
            contentPane.getChildren().setAll(settingsView);
            lblContentTitle.setText("Paramètres");
            lblStatus.setText("Paramètres chargés avec succès");
        } catch (IOException e) {
            System.err.println("Erreur de navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showHelp() {
        // Highlight the selected button
        setActiveButton(btnHelp);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/Aide.fxml"));
            Parent helpView = loader.load();
            setContent(helpView);
            updateTitle("Aide", "Support et assistance utilisateur");
            setStatusMessage("Aide chargée avec succès");
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            setStatusMessage("Veuillez entrer un terme de recherche");
            return;
        }
        
        updateTitle("Résultats de recherche", "Recherche pour: " + searchTerm);
        
        // Implement search functionality here
        VBox searchResults = new VBox();
        searchResults.setAlignment(javafx.geometry.Pos.CENTER);
        searchResults.setSpacing(10);
        searchResults.setPadding(new javafx.geometry.Insets(20));
        
        Label lblSearchTitle = new Label("Recherche pour: " + searchTerm);
        lblSearchTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label lblNoResults = new Label("Aucun résultat trouvé pour: " + searchTerm);
        lblNoResults.setStyle("-fx-text-fill: #7f8c8d;");
        
        searchResults.getChildren().addAll(lblSearchTitle, lblNoResults);
        setContent(searchResults);
        setStatusMessage("Recherche effectuée pour: " + searchTerm);
    }

    /**
     * Set the active button in the sidebar
     * @param activeButton The button to highlight as active
     */
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        btnOverview.getStyleClass().remove("active-nav-button");
        btnManageSpaces.getStyleClass().remove("active-nav-button");
        btnViewMap.getStyleClass().remove("active-nav-button");
        btnReservations.getStyleClass().remove("active-nav-button");
        btnAnalytics.getStyleClass().remove("active-nav-button");
        btnGalleryView.getStyleClass().remove("active-nav-button");
        btnMyReservations.getStyleClass().remove("active-nav-button");
        btnSearch.getStyleClass().remove("active-nav-button");
        btnSettings.getStyleClass().remove("active-nav-button");
        btnHelp.getStyleClass().remove("active-nav-button");

        // Add active class to the selected button
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-nav-button");
        }
    }
    
    /**
     * Set the content title in the header
     */
    private void updateTitle(String title, String subtitle) {
        if (lblContentTitle != null) {
            lblContentTitle.setText(title);
        }
        // Si vous avez un sous-titre dans votre interface, vous pouvez également le mettre à jour ici
    }
    
    /**
     * Set the status message in the footer
     * @param message The message to display
     */
    private void setStatusMessage(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }

    /**
     * Set the content of the main area
     * @param content The content to display
     */
    private void setContent(Parent content) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);
    }

    /**
     * Show an error view when content fails to load
     * @param errorMessage The error message to display
     */
    private void showErrorView(String errorMessage) {
        VBox errorView = new VBox();
        errorView.setAlignment(javafx.geometry.Pos.CENTER);
        errorView.setSpacing(10);
        errorView.setPadding(new javafx.geometry.Insets(20));

        Label lblError = new Label("Erreur de chargement");
        lblError.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label lblErrorMessage = new Label(errorMessage);
        lblErrorMessage.setStyle("-fx-text-fill: #7f8c8d;");
        lblErrorMessage.setWrapText(true);

        errorView.getChildren().addAll(lblError, lblErrorMessage);
        setContent(errorView);
    }

    /**
     * Handle navigation errors consistently
     */
    private void handleNavigationError(IOException e) {
        System.err.println("Erreur de navigation: " + e.getMessage());
        e.printStackTrace();
        showErrorView("Impossible de charger la vue: " + e.getMessage());
        setStatusMessage("Erreur: Impossible de charger la vue");
    }

    /**
     * Set the current user and update the UI
     * @param username The username to display
     */
    public void setCurrentUser(String username) {
        this.currentUser = username;
        lblUsername.setText(username);
    }
}
