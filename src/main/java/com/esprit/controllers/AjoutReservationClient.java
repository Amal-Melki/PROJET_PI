package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AjoutReservationClient implements Initializable {

    @FXML
    private ComboBox<Materiels> cbMateriel;
    @FXML
    private DatePicker dpDebut;
    @FXML
    private DatePicker dpFin;
    @FXML
    private TextField tfQuantite;
    @FXML
    private TextField tfMontantTotal;
    @FXML
    private Button btnReserver;
    @FXML
    private Button btnRetour;
    @FXML
    private ImageView logoImage;
    @FXML private Button btnRetourAccueil;


    private final int clientId = 1; // ID fictif du client connecté (À gérer si vous avez un vrai système d'authentification)
    private Materiels materielSelectionne;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfMontantTotal.setEditable(false);

        tfQuantite.textProperty().addListener((obs, oldVal, newVal) -> calculerMontantTotal());
        cbMateriel.valueProperty().addListener((obs, oldMat, newMat) -> calculerMontantTotal());

        cbMateriel.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Materiels item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });

        cbMateriel.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Materiels item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });

        List<Materiels> materiels = new ServiceMateriel().rechercher().stream()
                .filter(m -> m.getQuantite() > 0 && "DISPONIBLE".equalsIgnoreCase(m.getEtat()))
                .collect(Collectors.toList());
        cbMateriel.getItems().setAll(materiels);

        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }


    @FXML
    private void materielChange() {
        materielSelectionne = cbMateriel.getSelectionModel().getSelectedItem();
        calculerMontantTotal();
    }


    public void setMaterielsDisponibles(List<Materiels> liste) {
        cbMateriel.getItems().setAll(liste);
    }

    public void setMaterielSelectionne(Materiels m) {
        this.materielSelectionne = m;
        if (cbMateriel != null && m != null) {
            cbMateriel.getSelectionModel().select(m);
        }
    }

    private void calculerMontantTotal() {
        Materiels materiel = cbMateriel.getValue();
        String quantiteText = tfQuantite.getText().trim();

        if (materiel != null && !quantiteText.isEmpty() && quantiteText.matches("\\d+")) {
            try {
                int quantite = Integer.parseInt(quantiteText);
                double montant = quantite * materiel.getPrix();
                tfMontantTotal.setText(String.format("%.2f", montant));
            } catch (NumberFormatException e) {
                tfMontantTotal.clear();
            }
        } else {
            tfMontantTotal.clear();
        }
    }

    @FXML
    void reserverMateriel() {
        Materiels materiel = cbMateriel.getValue();

        if (materiel == null || dpDebut.getValue() == null || dpFin.getValue() == null || tfQuantite.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        LocalDate dateDebut = dpDebut.getValue();
        LocalDate dateFin = dpFin.getValue();

        LocalDate today = LocalDate.now();
        if (dateDebut.isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être antérieure à aujourd’hui.");
            return;
        }

        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "La date de début doit précéder la date de fin.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(tfQuantite.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide.");
            return;
        }

        if (quantite <= 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être positive.");
            return;
        }

        if (!"DISPONIBLE".equalsIgnoreCase(materiel.getEtat())) {
            showAlert(Alert.AlertType.WARNING, "Indisponible", "Ce matériel n'est pas disponible.");
            return;
        }

        ServiceMateriel sm = new ServiceMateriel();
        int quantiteStock = sm.getQuantiteById(materiel.getId());
        if (quantite > quantiteStock) {
            showAlert(Alert.AlertType.WARNING, "Stock insuffisant", "Il ne reste que " + quantiteStock + " unités disponibles.");
            return;
        }

        ServiceReservationMateriel serviceReservation = new ServiceReservationMateriel();
        // CORRECTION ICI : Suppression de `&& r.getId() != reservation.getId()`
        // car 'reservation' n'est pas encore définie et ce n'est pas une modification.
        for (ReservationMateriel r : serviceReservation.rechercher()) {
            if (r.getMaterielId() == materiel.getId()) { // Pas besoin de r.getId() != reservation.getId() pour une nouvelle réservation
                // Logique de détection de chevauchement de dates
                if (!(r.getDateFin().isBefore(dateDebut) || r.getDateDebut().isAfter(dateFin))) {
                    showAlert(Alert.AlertType.ERROR, "Conflit", "Ce matériel est déjà réservé sur cette période.");
                    return;
                }
            }
        }

        // La variable 'reservation' est déclarée ICI, après la boucle de vérification.
        ReservationMateriel reservation = new ReservationMateriel(
                0, // ID à 0, il sera généré par la base de données
                materiel.getId(),
                dateDebut,
                dateFin,
                quantite,
                "EN_ATTENTE", // Statut par défaut
                Double.parseDouble(tfMontantTotal.getText().replace(",", ".")), // Récupérer le montant affiché et convertir
                clientId // L'ID du client connecté
        );

        // Appel de la méthode 'ajouter' (celle qui prend id_client)
        new ServiceReservationMateriel().ajouter(reservation);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation enregistrée !");
        resetForm();
    }

    private void resetForm() {
        tfQuantite.clear();
        tfMontantTotal.clear();
        dpDebut.setValue(null);
        dpFin.setValue(null);
        cbMateriel.getSelectionModel().clearSelection();
    }

    @FXML
    void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Navigation.fxml"));
            Parent root = loader.load();

            NavigationController navController = loader.getController();
            navController.setAdminMode(false); // si tu veux l’accueil client
            navController.setCurrentUser(NavigationController.getCurrentClient());

            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("EventHub");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}