package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
    private TextField tfPrix;

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnRetour;

    private Materiels materiel;
    @FXML
    private Button btnModifierImage;

    @FXML
    private Label lblImageModifPath;

    @FXML
    private ImageView imageViewMateriel;

    private String cheminImageActuelle;


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

    private String ancienneImage;

    public void setMateriel(Materiels m) {
        this.materiel = m;
        ancienneImage = m.getImage(); // conserve l'image d'origine




        tfNom.setText(m.getNom());
        cbType.setValue(m.getType());
        cbEtat.setValue(m.getEtat());
        tfQuantite.setText(String.valueOf(m.getQuantite()));
        tfPrix.setText(String.valueOf(m.getPrix()));
        taDescription.setText(m.getDescription());

        // ✅ Afficher l'image actuelle si elle existe
        if (m.getImage() != null && !m.getImage().isEmpty()) {
            cheminImageActuelle = m.getImage();
            lblImageModifPath.setText(new File(cheminImageActuelle).getName());
            imageViewMateriel.setImage(new javafx.scene.image.Image(new File(cheminImageActuelle).toURI().toString()));
        }
    }


    @FXML
    void modifierMateriel(ActionEvent event) {
        try {
            materiel.setNom(tfNom.getText());
            materiel.setType(cbType.getValue());
            materiel.setEtat(cbEtat.getValue());
            materiel.setQuantite(Integer.parseInt(tfQuantite.getText()));
            materiel.setPrix(Double.parseDouble(tfPrix.getText()));
            materiel.setDescription(taDescription.getText());

            // 🔁 image : nouvelle ou ancienne
            if (cheminImageActuelle != null) {
                materiel.setImage(cheminImageActuelle);
            } else {
                materiel.setImage(ancienneImage);
            }

            // ✅ maintenant on peut sauvegarder l’objet mis à jour
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
            alert.setContentText("Quantité ou prix invalide !");
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

    @FXML
    private void modifierImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une nouvelle image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(btnModifierImage.getScene().getWindow());

        if (file != null) {
            cheminImageActuelle = file.getAbsolutePath();
            lblImageModifPath.setText(file.getName());
            imageViewMateriel.setImage(new Image(file.toURI().toString()));
        }
    }


}
