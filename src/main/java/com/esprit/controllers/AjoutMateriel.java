package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    @FXML
    private Button btnRetour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        cbEtat.setValue("DISPONIBLE");

        btnAjouter.setStyle(
                "-fx-background-color: #ff8cb3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4 10 4 10;"
        );
    }

    @FXML
    void addMateriel(ActionEvent event) throws IOException {
        try {
            String nom = tfNom.getText().trim();
            String type = cbType.getValue();
            String quantiteStr = tfQuantite.getText().trim();
            String etat = cbEtat.getValue();
            String description = taDescription.getText().trim();

            // Vérification champ vide
            if (nom.isEmpty() || type == null || quantiteStr.isEmpty() || etat == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            int quantite = Integer.parseInt(quantiteStr);

            //  Vérification de la quantité
            if (quantite < 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité ne peut pas être négative.");
                return;
            }
            if (quantite == 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être supérieure à 0.");
                return;
            }

            //  Vérification de l'existence
            ServiceMateriel sm = new ServiceMateriel();
            List<Materiels> existants = sm.recuperer();
            for (Materiels m : existants) {
                if (m.getNom().equalsIgnoreCase(nom)
                        && m.getType().equalsIgnoreCase(type)
                        && m.getQuantite() == quantite) {

                    showAlert(Alert.AlertType.WARNING, "Doublon", "Ce matériel existe déjà !");
                    return;
                }
            }

            // ✅ Ajout
            Materiels materiel = new Materiels(0, nom, type, quantite, etat, description);
            sm.ajouter(materiel);

            showAlert(Alert.AlertType.INFORMATION, "Ajout réussi", "Matériel ajouté avec succès !");

            // Réinitialisation des champs
            tfNom.clear();
            tfQuantite.clear();
            taDescription.clear();
            cbType.getSelectionModel().clearSelection();
            cbEtat.setValue("DISPONIBLE");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être un nombre entier valide.");
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            // Remplacer la scène actuelle
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
