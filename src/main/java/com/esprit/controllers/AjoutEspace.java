package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class AjoutEspaceController {

    @FXML
    private void ajouterEspace() {
        // Code pour ajouter l'espace (par exemple, sauvegarder dans la base de données)
        // Simuler un ajout réussi (ici, un message d'alerte pour l'exemple)
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("L'espace a été ajouté avec succès!");
        alert.showAndWait();
    }

    @FXML
    private void annulerAjout() {
        // Code pour annuler l'ajout et fermer ou réinitialiser les champs
        // Simuler une annulation (ici, un message d'alerte pour l'exemple)
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Annulation");
        alert.setHeaderText(null);
        alert.setContentText("Ajout annulé!");
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
