package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ModifierMaterielFormulaire {

    @FXML
    private TextField tfNom;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private ComboBox<String> cbEtat;

    @FXML
    private TextField tfQuantite;

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnRetour;

    private Materiels materiel;

    @FXML
    public void initialize() {
        cbType.getItems().addAll(
                "Mobilier (chaises, tables, estrades)",
                "Éclairage (spots, projecteurs, LED)",
                "Sonorisation (micros, enceintes, mixeurs)",
                "Audiovisuel (écran, vidéoprojecteur, caméra)",
                "Décoration (fleurs, supports décoratifs, rideaux)",
                "Tentes / Structures",
                "Équipements de scène",
                "Équipements de sécurité (extincteurs, barrières)",
                "Électricité (générateurs, rallonges)",
                "Photobooth / Accessoires photo"
        );

        cbEtat.getItems().addAll("DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE");
    }

    // ✅ Cette méthode est appelée pour transmettre les données
    public void setMateriel(Materiels m) {
        this.materiel = m;

        tfNom.setText(m.getNom());
        cbType.setValue(m.getType());
        cbEtat.setValue(m.getEtat());
        tfQuantite.setText(String.valueOf(m.getQuantite()));
        taDescription.setText(m.getDescription());
    }

    @FXML
    void modifierMateriel(ActionEvent event) {
        try {
            materiel.setNom(tfNom.getText());
            materiel.setType(cbType.getValue());
            materiel.setEtat(cbEtat.getValue());
            materiel.setQuantite(Integer.parseInt(tfQuantite.getText()));
            materiel.setDescription(taDescription.getText());

            ServiceMateriel service = new ServiceMateriel();
            service.modifier(materiel);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Matériel modifié avec succès !");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Quantité invalide !");
            alert.show();
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMateriel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
