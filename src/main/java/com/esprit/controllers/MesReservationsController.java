package com.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MesReservationsController {

    @FXML
    private Button btnRefresh;

    @FXML
    private ComboBox<String> comboStatusFilter;

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, Integer> colId;

    @FXML
    private TableColumn<Reservation, String> colSpace;

    @FXML
    private TableColumn<Reservation, LocalDate> colStartDate;

    @FXML
    private TableColumn<Reservation, LocalDate> colEndDate;

    @FXML
    private TableColumn<Reservation, Integer> colPeople;

    @FXML
    private TableColumn<Reservation, String> colPhone;

    @FXML
    private TableColumn<Reservation, String> colDescription;

    @FXML
    private TableColumn<Reservation, String> colStatus;

    @FXML
    private TableColumn<Reservation, Void> colActions;

    @FXML
    private VBox emptyStateContainer;

    private ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialisation des colonnes avec les propriétés de Reservation
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colSpace.setCellValueFactory(data -> data.getValue().espaceProperty());
        colStartDate.setCellValueFactory(data -> data.getValue().dateDebutProperty());
        colEndDate.setCellValueFactory(data -> data.getValue().dateFinProperty());
        colPeople.setCellValueFactory(data -> data.getValue().personnesProperty().asObject());
        colPhone.setCellValueFactory(data -> data.getValue().telephoneProperty());
        colDescription.setCellValueFactory(data -> data.getValue().descriptionProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statutProperty());

        // Charger les données simulées (remplace par un appel à ta source de données)
        loadReservations();

        // Remplir la table
        reservationsTable.setItems(allReservations);

        // Initialiser le comboBox pour le filtre de statut (ajout de "Terminé")
        comboStatusFilter.getItems().addAll("Tous", "Confirmé", "Annulé", "En attente", "Terminé");
        comboStatusFilter.setValue("Tous");

        // Écoute du filtre par statut
        comboStatusFilter.setOnAction(e -> filterReservations());

        // Vérifie si la liste est vide pour afficher le message "empty state"
        updateEmptyState();
    }

    private void loadReservations() {
        // Exemple de données factices
        allReservations.clear();
        allReservations.addAll(
                new Reservation(1, "Salle A", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 10, "12345678", "Réunion", "Confirmé"),
                new Reservation(2, "Salle B", LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), 20, "87654321", "Conférence", "En attente"),
                new Reservation(3, "Salle C", LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), 15, "11223344", "Formation terminée", "Terminé")
        );
    }

    @FXML
    private void refreshReservations() {
        // TODO: Remplacer par ta logique réelle de rafraîchissement (ex: requête base de données)
        loadReservations();
        filterReservations();
        updateEmptyState();
    }

    private void filterReservations() {
        String selectedStatus = comboStatusFilter.getValue();
        if (selectedStatus == null || selectedStatus.equals("Tous")) {
            reservationsTable.setItems(allReservations);
        } else {
            Predicate<Reservation> filter = r -> r.getStatut().equalsIgnoreCase(selectedStatus);
            reservationsTable.setItems(allReservations.stream().filter(filter).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean empty = reservationsTable.getItems().isEmpty();
        emptyStateContainer.setVisible(empty);
        emptyStateContainer.setManaged(empty);
        reservationsTable.setVisible(!empty);
    }

    @FXML
    private void showGalleryView() {
        // TODO: Implémenter la navigation vers la galerie des espaces
        System.out.println("Navigation vers la galerie des espaces...");
    }

    // Classe interne simple pour modèle Reservation (à mettre dans son propre fichier normalement)
    public static class Reservation {
        private final javafx.beans.property.IntegerProperty id;
        private final javafx.beans.property.StringProperty espace;
        private final javafx.beans.property.ObjectProperty<LocalDate> dateDebut;
        private final javafx.beans.property.ObjectProperty<LocalDate> dateFin;
        private final javafx.beans.property.IntegerProperty personnes;
        private final javafx.beans.property.StringProperty telephone;
        private final javafx.beans.property.StringProperty description;
        private final javafx.beans.property.StringProperty statut;

        public Reservation(int id, String espace, LocalDate dateDebut, LocalDate dateFin, int personnes,
                           String telephone, String description, String statut) {
            this.id = new javafx.beans.property.SimpleIntegerProperty(id);
            this.espace = new javafx.beans.property.SimpleStringProperty(espace);
            this.dateDebut = new javafx.beans.property.SimpleObjectProperty<>(dateDebut);
            this.dateFin = new javafx.beans.property.SimpleObjectProperty<>(dateFin);
            this.personnes = new javafx.beans.property.SimpleIntegerProperty(personnes);
            this.telephone = new javafx.beans.property.SimpleStringProperty(telephone);
            this.description = new javafx.beans.property.SimpleStringProperty(description);
            this.statut = new javafx.beans.property.SimpleStringProperty(statut);
        }

        // Getters JavaFX properties pour TableView bindings
        public javafx.beans.property.IntegerProperty idProperty() { return id; }
        public javafx.beans.property.StringProperty espaceProperty() { return espace; }
        public javafx.beans.property.ObjectProperty<LocalDate> dateDebutProperty() { return dateDebut; }
        public javafx.beans.property.ObjectProperty<LocalDate> dateFinProperty() { return dateFin; }
        public javafx.beans.property.IntegerProperty personnesProperty() { return personnes; }
        public javafx.beans.property.StringProperty telephoneProperty() { return telephone; }
        public javafx.beans.property.StringProperty descriptionProperty() { return description; }
        public javafx.beans.property.StringProperty statutProperty() { return statut; }

        // Getters normaux
        public int getId() { return id.get(); }
        public String getEspace() { return espace.get(); }
        public LocalDate getDateDebut() { return dateDebut.get(); }
        public LocalDate getDateFin() { return dateFin.get(); }
        public int getPersonnes() { return personnes.get(); }
        public String getTelephone() { return telephone.get(); }
        public String getDescription() { return description.get(); }
        public String getStatut() { return statut.get(); }
    }
}
