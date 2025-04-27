package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.modules.ReservationEspace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AjoutReservationController implements Initializable {

    @FXML
    private ComboBox<String> espaceCombo;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;

    private EspaceService espaceService = new EspaceService();
    private ReservationEspaceService reservationService = new ReservationEspaceService();
    private Map<String, Integer> espaceIdMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerEspaces();
    }

    private void chargerEspaces() {
        List<Espace> espaces = espaceService.getAll();
        for (Espace espace : espaces) {
            if (espace.isDisponibilite()) {
                String espaceInfo = espace.getNom() + " (" + espace.getType() + ")";
                espaceCombo.getItems().add(espaceInfo);
                espaceIdMap.put(espaceInfo, espace.getId());
            }
        }
    }

    @FXML
    private void retourMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuEspace.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void ajouterReservation(ActionEvent event) {
        String espaceSelection = espaceCombo.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (espaceSelection == null || dateDebut == null || dateFin == null) {
            showAlert(AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (dateFin.isBefore(dateDebut)) {
            showAlert(AlertType.ERROR, "Erreur", "Dates invalides",
                    "La date de fin doit être après la date de début.");
            return;
        }

        Integer espaceId = espaceIdMap.get(espaceSelection);
        if (espaceId == null) {
            showAlert(AlertType.ERROR, "Erreur", "Espace non trouvé",
                    "L'espace sélectionné n'est pas valide.");
            return;
        }

        // Utilisateur connecté (à adapter selon votre système d'authentification)
        String utilisateur = "Utilisateur actuel"; // À remplacer par le nom réel

        ReservationEspace reservation = new ReservationEspace(espaceId, utilisateur, dateDebut, dateFin);
        reservationService.add(reservation);

        showAlert(AlertType.INFORMATION, "Succès", "Réservation ajoutée",
                "La réservation a été ajoutée avec succès.");

        // Réinitialisation des champs
        espaceCombo.setValue(null);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
    }

    @FXML
    private void annulerReservation(ActionEvent event) {
        espaceCombo.setValue(null);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}