package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    private Materiels materiel; // Objet à modifier

    // Initialisation automatique des ComboBox
    @FXML
    public void initialize() {
        cbType.getItems().addAll("Informatique", "Électrique", "Bureau", "Médical", "Audio");
        cbEtat.getItems().addAll("DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE");
    }

    // Méthode appelée depuis ModifierMateriel.java pour remplir les champs
    public void initData(Materiels m) {
        this.materiel = m;

        tfNom.setText(m.getNom());
        cbType.setValue(m.getType());
        cbEtat.setValue(m.getEtat());
        tfQuantite.setText(String.valueOf(m.getQuantite()));
        taDescription.setText(m.getDescription());
    }

    // Bouton "Enregistrer" cliqué
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

            // Fermer la fenêtre
            Stage stage = (Stage) btnModifier.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Quantité invalide !");
            alert.show();
        }
    }
}
