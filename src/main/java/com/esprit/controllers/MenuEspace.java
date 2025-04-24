package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class MenuEspaceController {

    @FXML
    private void ouvrirAjouterEspace() {
        try {
            // Charger la vue d'ajout d'espace
            Parent root = FXMLLoader.load(getClass().getResource("/views/AjouterEspace.fxml"));
            Stage stage = (Stage) btnAjouterEspace.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherEspaces() {
        // Code pour afficher les espaces (par exemple, ouvrir une liste ou tableau)
    }

    @FXML
    private void retourAccueil() {
        // Retour à l'écran principal ou à un autre écran de l'application
    }
}
