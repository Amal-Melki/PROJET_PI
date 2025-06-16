package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceMateriel;
import com.esprit.services.ServiceReservationMateriel;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

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

    private final int clientId = 1; // ID fictif du client connecté
    private Materiels materielSelectionne;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfMontantTotal.setEditable(false);

        tfQuantite.textProperty().addListener((obs, oldVal, newVal) -> calculerMontantTotal());

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

        // ✅ Remplissage automatique si vide
        if (cbMateriel.getItems().isEmpty()) {
            List<Materiels> materiels = new ServiceMateriel().recuperer().stream()
                    .filter(m -> m.getQuantite() > 0 && "DISPONIBLE".equalsIgnoreCase(m.getEtat()))
                    .toList();
            cbMateriel.getItems().setAll(materiels);
        }
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
            calculerMontantTotal();
        }
    }

    private void calculerMontantTotal() {
        Materiels materiel = cbMateriel.getValue();
        String quantiteText = tfQuantite.getText().trim();

        if (materiel != null && !quantiteText.isEmpty() && quantiteText.matches("\\d+")) {
            int quantite = Integer.parseInt(quantiteText);
            double montant = quantite * materiel.getPrix();
            tfMontantTotal.setText(String.format("%.2f", montant));
        } else {
            tfMontantTotal.clear();
        }
    }

    @FXML
    void reserverMateriel() {
        // 🔄 On récupère directement la sélection actuelle
        Materiels materiel = cbMateriel.getValue();

        if (materiel == null || dpDebut.getValue() == null || dpFin.getValue() == null || tfQuantite.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        LocalDate today = LocalDate.now();
        if (dpDebut.getValue().isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être antérieure à aujourd’hui.");
            return;
        }

        if (!dpDebut.getValue().isBefore(dpFin.getValue())) {
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

        ReservationMateriel reservation = new ReservationMateriel();
        reservation.setMaterielId(materiel.getId());
        reservation.setDateDebut(Date.valueOf(dpDebut.getValue()));
        reservation.setDateFin(Date.valueOf(dpFin.getValue()));
        reservation.setQuantiteReservee(quantite);
        reservation.setStatut("EN_ATTENTE");
        reservation.setIdClient(clientId);

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
    private void retourAccueil() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Accueil.fxml"));
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
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
