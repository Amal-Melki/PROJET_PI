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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AjoutMateriel implements Initializable {

    @FXML private TextField tfNom;
    @FXML private ComboBox<String> cbType;
    @FXML private TextField tfQuantite;
    @FXML private ComboBox<String> cbEtat;
    @FXML private TextArea taDescription;
    @FXML private TextField tfPrix;
    @FXML private Label lblImagePath;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;
    @FXML private Button btnImage;
    @FXML private ImageView imageMateriel;
    @FXML private VBox imageBox;
    @FXML
    private ImageView logoImage;

    private String cheminImage = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image img = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
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

        btnAjouter.setStyle("-fx-background-color: #ff8cb3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 4 10 4 10;");

    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(btnImage.getScene().getWindow());
        if (file != null) {
            cheminImage = file.getAbsolutePath();
            lblImagePath.setText(file.getName());

            Image image = new Image(file.toURI().toString());
            imageMateriel.setImage(image);

            imageBox.setVisible(true);
            imageBox.setManaged(true);
        }
    }

    @FXML
    void addMateriel(ActionEvent event) throws IOException {
        try {
            String nom = tfNom.getText().trim();
            String type = cbType.getValue();
            String quantiteStr = tfQuantite.getText().trim();
            String prixStr = tfPrix.getText().trim();
            String etat = cbEtat.getValue();
            String description = taDescription.getText().trim();

            if (nom.isEmpty() || type == null || quantiteStr.isEmpty() || prixStr.isEmpty() || etat == null || cheminImage == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires, y compris le prix et l’image.");
                return;
            }

            int quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être positive.");
                return;
            }

            double prix = Double.parseDouble(prixStr);
            if (prix < 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être supérieur ou égal à zéro.");
                return;
            }

            ServiceMateriel sm = new ServiceMateriel();
            for (Materiels m : sm.rechercher()) {
                if (m.getNom().equalsIgnoreCase(nom) &&
                        m.getType().equalsIgnoreCase(type) &&
                        m.getQuantite() == quantite &&
                        m.getPrix() == prix) {
                    showAlert(Alert.AlertType.WARNING, "Doublon", "Ce matériel existe déjà !");
                    return;
                }
            }

            Materiels materiel = new Materiels(0, nom, type, quantite, etat, description, cheminImage, prix);
            sm.ajouter(materiel);

            showAlert(Alert.AlertType.INFORMATION, "Ajout réussi", "Matériel ajouté avec succès !");
            resetForm();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Quantité et prix doivent être des nombres valides.");
        }
    }

    private void resetForm() {
        tfNom.clear();
        tfQuantite.clear();
        tfPrix.clear();
        taDescription.clear();
        cbType.getSelectionModel().clearSelection();
        cbEtat.setValue("DISPONIBLE");
        cheminImage = null;
        lblImagePath.setText("");
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
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
