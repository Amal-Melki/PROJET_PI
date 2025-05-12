package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ModifierEspaceAdminController {

    public Button btnEnregistrer;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfType;
    @FXML
    private TextField tfCapacite;
    @FXML
    private TextField tfLocalisation;
    @FXML
    private TextField tfPrix;

    private Espace espace; // à stocker pour modifier plus tard

    public void setEspace(Espace espace) {
        this.espace = espace;
        tfNom.setText(espace.getNom());
        tfType.setText(espace.getType());
        tfCapacite.setText(String.valueOf(espace.getCapacite()));
        tfLocalisation.setText(espace.getLocalisation());
        tfPrix.setText(String.valueOf(espace.getPrix()));
    }

    @FXML
    void modifierEspace() {
        // Validation des champs
        String nom = tfNom.getText();
        String type = tfType.getText();
        String capaciteStr = tfCapacite.getText();
        String localisation = tfLocalisation.getText();
        String prixStr = tfPrix.getText();

        if (nom == null || nom.trim().isEmpty()) {
            showAlert("Le champ Nom est obligatoire.");
            return;
        }
        if (type == null || type.trim().isEmpty()) {
            showAlert("Le champ Type est obligatoire.");
            return;
        }
        if (localisation == null || localisation.trim().isEmpty()) {
            showAlert("Le champ Localisation est obligatoire.");
            return;
        }
        int capacite;
        try {
            capacite = Integer.parseInt(capaciteStr);
            if (capacite <= 0) {
                showAlert("La Capacité doit être un entier positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("La Capacité doit être un entier valide.");
            return;
        }
        double prix;
        try {
            prix = Double.parseDouble(prixStr);
            if (prix < 0) {
                showAlert("Le Prix doit être un nombre positif ou nul.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Le Prix doit être un nombre valide.");
            return;
        }

        // Mise à jour de l'objet espace
        espace.setNom(nom);
        espace.setType(type);
        espace.setCapacite(capacite);
        espace.setLocalisation(localisation);
        espace.setPrix(prix);

        // Sauvegarder dans la base de données
        new EspaceService().update(espace);

        // Fermer la fenêtre
        tfNom.getScene().getWindow().hide();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void retourMenu() {
        tfNom.getScene().getWindow().hide();
    }
}
