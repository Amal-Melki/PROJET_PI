package com.esprit.controllers;

import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
public class AjouterEspaceAdminController implements Initializable {

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

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnRetour;

    private final EspaceService espaceService = new EspaceService();

    private Espace currentEspace = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization if needed
    }

    public void setEspace(Espace espace) {
        this.currentEspace = espace;
        if (espace != null) {
            tfNom.setText(espace.getNom());
            tfType.setText(espace.getType());
            tfCapacite.setText(String.valueOf(espace.getCapacite()));
            tfLocalisation.setText(espace.getLocalisation());
            tfPrix.setText(String.valueOf(espace.getPrix()));
            btnAjouter.setText("Modifier");
        }
    }

    @FXML
    public void ajouterEspace() {
        String nom = tfNom.getText().trim();
        String type = tfType.getText().trim();
        String capaciteText = tfCapacite.getText().trim();
        String localisation = tfLocalisation.getText().trim();
        String prixText = tfPrix.getText().trim();

        if (nom.isEmpty() || type.isEmpty() || capaciteText.isEmpty() || localisation.isEmpty() || prixText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        int capacite;
        double prix;
        try {
            capacite = Integer.parseInt(capaciteText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Capacité invalide", "La capacité doit être un nombre entier.");
            return;
        }

        try {
            prix = Double.parseDouble(prixText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre décimal.");
            return;
        }

        if (currentEspace == null) {
            currentEspace = new Espace();
        }
        currentEspace.setNom(nom);
        currentEspace.setType(type);
        currentEspace.setCapacite(capacite);
        currentEspace.setLocalisation(localisation);
        currentEspace.setPrix(prix);

        if (currentEspace.getId() == 0) {
            espaceService.add(currentEspace);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Espace ajouté avec succès !");
        } else {
            espaceService.update(currentEspace);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Espace modifié avec succès !");
        }

        resetForm();
        currentEspace = null;
        btnAjouter.setText("Ajouter");
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void resetForm() {
        tfNom.clear();
        tfType.clear();
        tfCapacite.clear();
        tfLocalisation.clear();
        tfPrix.clear();
    }

    @FXML
    private void retourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuEspace.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
