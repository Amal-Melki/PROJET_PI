package com.esprit.core.controllers.controllers;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TableauDeBordController implements Initializable {

    @FXML
    private Label lblTotalSpaces;
    
    @FXML
    private Label lblAvailableSpaces;
    
    @FXML
    private Label lblReservedSpaces;
    
    @FXML
    private Label lblTotalReservations;
    
    @FXML
    private PieChart pieChartSpaceTypes;
    
    @FXML
    private BarChart<String, Number> barChartReservations;
    
    @FXML
    private VBox recentActivityContainer;
    
    private final EspaceService espaceService = new EspaceService();
    private final ReservationEspaceService reservationService = new ReservationEspaceService();

    private IntegerProperty totalSpaces = new SimpleIntegerProperty();
    private IntegerProperty availableSpaces = new SimpleIntegerProperty();
    private IntegerProperty reservedSpaces = new SimpleIntegerProperty();
    private IntegerProperty totalReservations = new SimpleIntegerProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load statistics and data visualizations
        loadStatistics();
        loadPieChart();
        loadBarChart();
        loadRecentActivity();

        // Bind statistics
        lblTotalSpaces.textProperty().bind(totalSpaces.asString());
        lblAvailableSpaces.textProperty().bind(availableSpaces.asString());
        lblReservedSpaces.textProperty().bind(reservedSpaces.asString());
        lblTotalReservations.textProperty().bind(totalReservations.asString());
    }
    
    private void loadStatistics() {
        try {
            // Charger les espaces depuis le service
            List<Espace> spaces = espaceService.getAll();
            
            // Count total spaces, available spaces, and reserved spaces
            int totalSpacesValue = spaces.size();
            long availableSpacesValue = spaces.stream().filter(Espace::isDisponible).count();
            long reservedSpacesValue = totalSpacesValue - availableSpacesValue;
            
            // Get total reservations
            List<ReservationEspace> reservations = reservationService.getAllReservations();
            int totalReservationsValue = reservations.size();
            
            // Update the properties
            totalSpaces.set(totalSpacesValue);
            availableSpaces.set((int) availableSpacesValue);
            reservedSpaces.set((int) reservedSpacesValue);
            totalReservations.set(totalReservationsValue);
            
            System.out.println("Statistiques chargées: " + totalSpacesValue + " espaces, " + 
                               availableSpacesValue + " disponibles, " + 
                               reservedSpacesValue + " réservés, " + 
                               totalReservationsValue + " réservations totales");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, afficher des valeurs par défaut
            totalSpaces.set(0);
            availableSpaces.set(0);
            reservedSpaces.set(0);
            totalReservations.set(0);
        }
    }
    
    private void loadPieChart() {
        try {
            // Group spaces by type
            List<Espace> spaces = espaceService.getAll();
            
            if (spaces.isEmpty()) {
                // Si aucun espace n'est trouvé, utiliser des données de démonstration
                pieChartSpaceTypes.setTitle("Répartition par Type d'Espace (Démo)");
                pieChartSpaceTypes.setData(createDummyPieChartData());
                return;
            }
            
            Map<String, Long> spaceTypeCount = spaces.stream()
                    .collect(Collectors.groupingBy(Espace::getType, Collectors.counting()));
            
            // Create pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            spaceTypeCount.forEach((type, count) -> 
                pieChartData.add(new PieChart.Data(type + " (" + count + ")", count))
            );
    
            pieChartSpaceTypes.setData(pieChartData);
            pieChartSpaceTypes.setTitle("Répartition par Type d'Espace");
            pieChartSpaceTypes.setLegendVisible(true);
            pieChartSpaceTypes.setLabelsVisible(true);
            pieChartSpaceTypes.setLabelLineLength(20);
            
            System.out.println("Graphique en secteurs chargé avec " + pieChartData.size() + " types d'espaces");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du graphique en secteurs: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, afficher des données de démonstration
            pieChartSpaceTypes.setTitle("Répartition par Type d'Espace (Démo)");
            pieChartSpaceTypes.setData(createDummyPieChartData());
        }
    }
    
    private ObservableList<PieChart.Data> createDummyPieChartData() {
        ObservableList<PieChart.Data> dummyData = FXCollections.observableArrayList();
        dummyData.add(new PieChart.Data("Conférence (3)", 3));
        dummyData.add(new PieChart.Data("Réunion (2)", 2));
        dummyData.add(new PieChart.Data("Extérieur (2)", 2));
        dummyData.add(new PieChart.Data("Studio (1)", 1));
        return dummyData;
    }
    
    private void loadBarChart() {
        try {
            // Récupérer les réservations
            List<ReservationEspace> reservations = reservationService.getAllReservations();
            
            if (reservations.isEmpty()) {
                // Si aucune réservation n'est trouvée, utiliser des données de démonstration
                barChartReservations.setTitle("Réservations Mensuelles (Démo)");
                barChartReservations.getData().add(createDummyBarChartSeries());
                return;
            }
            
            // Compter les réservations par mois
            Map<Month, Long> reservationsByMonth = reservations.stream()
                    .collect(Collectors.groupingBy(
                            res -> res.getDateDebut().getMonth(),
                            Collectors.counting()
                    ));
            
            // Créer la série de données
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Réservations");
            
            // Ajouter les données pour les 6 derniers mois
            LocalDate now = LocalDate.now();
            for (int i = 5; i >= 0; i--) {
                Month month = now.minusMonths(i).getMonth();
                Long count = reservationsByMonth.getOrDefault(month, 0L);
                series.getData().add(new XYChart.Data<>(getMonthAbbreviation(month), count));
            }
            
            barChartReservations.getData().clear();
            barChartReservations.getData().add(series);
            barChartReservations.setTitle("Réservations Mensuelles");
            
            System.out.println("Graphique à barres chargé avec les données de réservation mensuelles");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du graphique à barres: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, afficher des données de démonstration
            barChartReservations.getData().clear();
            barChartReservations.setTitle("Réservations Mensuelles (Démo)");
            barChartReservations.getData().add(createDummyBarChartSeries());
        }
    }
    
    private XYChart.Series<String, Number> createDummyBarChartSeries() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réservations");
        
        // Add last 6 months of data
        series.getData().add(new XYChart.Data<>("Jan", 15));
        series.getData().add(new XYChart.Data<>("Fév", 20));
        series.getData().add(new XYChart.Data<>("Mar", 25));
        series.getData().add(new XYChart.Data<>("Avr", 22));
        series.getData().add(new XYChart.Data<>("Mai", 30));
        series.getData().add(new XYChart.Data<>("Juin", 28));
        
        return series;
    }
    
    private String getMonthAbbreviation(Month month) {
        switch (month) {
            case JANUARY: return "Jan";
            case FEBRUARY: return "Fév";
            case MARCH: return "Mar";
            case APRIL: return "Avr";
            case MAY: return "Mai";
            case JUNE: return "Juin";
            case JULY: return "Juil";
            case AUGUST: return "Août";
            case SEPTEMBER: return "Sep";
            case OCTOBER: return "Oct";
            case NOVEMBER: return "Nov";
            case DECEMBER: return "Déc";
            default: return "";
        }
    }
    
    private void loadRecentActivity() {
        try {
            // Effacer le contenu existant
            recentActivityContainer.getChildren().clear();
            
            // Récupérer les dernières réservations
            List<ReservationEspace> recentReservations = reservationService.getAllReservations();
            
            if (recentReservations.isEmpty()) {
                // Si aucune activité récente n'est trouvée, utiliser des données de démonstration
                loadDummyActivity();
                return;
            }
            
            // Trier par date de réservation (décroissant)
            recentReservations.sort(Comparator.comparing(ReservationEspace::getDateDebut).reversed());
            
            // Limiter à 5 activités
            int maxActivities = Math.min(recentReservations.size(), 5);
            List<ReservationEspace> limitedReservations = recentReservations.subList(0, maxActivities);
            
            // Récupérer les détails des espaces pour les réservations
            Map<Integer, Espace> espacesMap = new HashMap<>();
            for (Espace espace : espaceService.getAll()) {
                espacesMap.put(espace.getEspaceId(), espace);
            }
            
            // Formatting
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (ReservationEspace reservation : limitedReservations) {
                // Créer la carte d'activité
                HBox activityCard = new HBox();
                activityCard.setSpacing(10);
                activityCard.setPadding(new Insets(10));
                activityCard.getStyleClass().add("activity-card");
                
                // Étiquette de date
                Text dateText = new Text(reservation.getDateDebut().format(formatter));
                dateText.getStyleClass().add("activity-date");
                
                // Description de l'activité
                String espaceName = "Espace inconnu";
                if (espacesMap.containsKey(reservation.getEspaceId())) {
                    espaceName = espacesMap.get(reservation.getEspaceId()).getNom();
                }
                
                String activityDesc = "Réservation de \"" + espaceName + "\" par " + 
                                       reservation.getNomClient() + " du " + 
                                       reservation.getDateDebut().format(formatter) + 
                                       " au " + reservation.getDateFin().format(formatter);
                
                Text activityText = new Text(activityDesc);
                activityText.getStyleClass().add("activity-description");
                
                activityCard.getChildren().addAll(dateText, activityText);
                recentActivityContainer.getChildren().add(activityCard);
            }
            
            System.out.println("Activités récentes chargées: " + limitedReservations.size() + " réservations");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des activités récentes: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, afficher des activités de démonstration
            loadDummyActivity();
        }
    }
    
    private void loadDummyActivity() {
        // Données de démonstration pour les activités récentes
        List<String> activities = new ArrayList<>();
        activities.add("Nouvelle réservation créée pour Grande Salle par le client Sophie Martin");
        activities.add("Espace 'Studio Photo' marqué comme indisponible pour maintenance");
        activities.add("Ajout d'un nouvel espace 'Terrasse Panoramique'");
        activities.add("Prix mis à jour pour 'Salle de Conférence' de 500 à 600 TND");
        activities.add("Réservation annulée pour Jardin Event");
        
        // Formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < activities.size(); i++) {
            // Create activity card
            HBox activityCard = new HBox();
            activityCard.setSpacing(10);
            activityCard.setPadding(new Insets(10));
            activityCard.getStyleClass().add("activity-card");
            
            // Date label (simulated recent dates)
            LocalDate activityDate = today.minusDays(i);
            Text dateText = new Text(activityDate.format(formatter));
            dateText.getStyleClass().add("activity-date");
            
            // Activity description
            Text activityText = new Text(activities.get(i));
            activityText.getStyleClass().add("activity-description");
            
            activityCard.getChildren().addAll(dateText, activityText);
            recentActivityContainer.getChildren().add(activityCard);
        }
    }
}
