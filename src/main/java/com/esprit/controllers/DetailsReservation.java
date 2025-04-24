package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class DetailsReservationController {

    @FXML
    private void modifierReservation() {
        // Code pour modifier la réservation
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Modification");
        alert.setHeaderText(null);
        alert.setContentText("Réservation modifiée avec succès!");
        alert.showAndWait();
    }

    @FXML
    private void retourMenuEspace() {
        try {
            // Charger la vue du menu espace
            Parent root = FXMLLoader.load(getClass().getResource("/views/MenuEspace.fxml"));
            Stage stage = (Stage) btnRetourMenu.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Problème lors de la navigation");
            alert.setContentText("Une erreur est survenue lors du retour au menu.");
            alert.showAndWait();
        }
    }
}
