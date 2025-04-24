package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;

public class AjouterReservationController {

    @FXML
    private void ajouterReservation() {
        // Code pour ajouter la réservation (enregistrer dans la base de données)
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Réservation ajoutée avec succès!");
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
