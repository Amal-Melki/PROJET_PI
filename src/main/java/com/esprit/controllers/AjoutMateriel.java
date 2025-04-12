package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AjoutMateriel implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private TextField tfQuantite;

    @FXML
    private ComboBox<String> cbEtat;

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnAjouter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.getItems().addAll("Informatique", "Électrique", "Bureau", "Médical");
        cbEtat.getItems().addAll("DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE");
        cbEtat.setValue("DISPONIBLE");
    }

    @FXML
    void addMateriel(ActionEvent event) throws IOException {
        try {
            String nom = tfNom.getText();
            String type = cbType.getValue();
            int quantite = Integer.parseInt(tfQuantite.getText());
            String etat = cbEtat.getValue();
            String description = taDescription.getText();

            Materiels materiel = new Materiels(0, nom, type, quantite, etat, description);
            ServiceMateriel sm = new ServiceMateriel();
            sm.ajouter(materiel);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ajout réussi");
            alert.setContentText("Matériel ajouté avec succès !");
            alert.show();

            // ✅ Rediriger vers ModifierMateriel.fxml après l’ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMateriel.fxml"));
            Parent root = loader.load();
            btnAjouter.getScene().setRoot(root);

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Quantité doit être un nombre entier.");
            alert.show();
        }
    }
}
