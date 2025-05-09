package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierMateriel implements Initializable {

    @FXML
    private FlowPane containerMateriels;

    @FXML
    private Button btnRetourAccueil;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final ObservableList<Materiels> materiels = FXCollections.observableArrayList();

    @FXML
    private TextField tfRecherche;
    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private ComboBox<String> cbFiltreType;
    @FXML private TextField tfFiltreQuantite;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServiceMateriel sm = new ServiceMateriel();
        materiels.setAll(sm.recuperer());
        afficherMateriels();
        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerMateriels(newValue);
        });
        cbFiltreStatut.setItems(FXCollections.observableArrayList("Tous", "DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE"));
        cbFiltreType.setItems(FXCollections.observableArrayList("Tous", "Mobilier", "Éclairage", "Sonorisation", "Audiovisuel"));
        cbFiltreStatut.setValue("Tous");
        cbFiltreType.setValue("Tous");

    }

    private void afficherMateriels() {
        containerMateriels.getChildren().clear();

        for (Materiels m : materiels) {
            VBox box = new VBox(8);
            box.setStyle("-fx-border-color: #ccc; -fx-background-color: white; -fx-padding: 10; -fx-alignment: center;");
            box.setPrefWidth(150);

            ImageView imageView = new ImageView();
            if (m.getImage() != null && !m.getImage().isEmpty()) {
                File file = new File(m.getImage());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    imageView.setImage(new Image("/images/nondispo.jpg"));
                }
            } else {
                imageView.setImage(new Image("/images/nondispo.jpg"));
            }

            imageView.setFitWidth(120);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            Label nomLabel = new Label(m.getNom());
            nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            RadioButton radioButton = new RadioButton();
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setUserData(m);

            box.getChildren().addAll(imageView, nomLabel, radioButton);
            containerMateriels.getChildren().add(box);
        }
    }

    @FXML
    void modifierMateriel(ActionEvent event) {
        RadioButton selectedRadio = (RadioButton) toggleGroup.getSelectedToggle();
        if (selectedRadio == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun matériel sélectionné", "Veuillez sélectionner un matériel à modifier.");
            return;
        }

        Materiels materiel = (Materiels) selectedRadio.getUserData();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMaterielFormulaire.fxml"));
            Parent root = loader.load();

            // Appel de setMateriel() du contrôleur pour pré-remplir le formulaire
            ModifierMaterielFormulaire controller = loader.getController();
            controller.setMateriel(materiel);

            Stage stage = (Stage) btnModifier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Matériel");
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerMateriel(ActionEvent event) {
        RadioButton selectedRadio = (RadioButton) toggleGroup.getSelectedToggle();
        if (selectedRadio == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun matériel sélectionné", "Veuillez sélectionner un matériel à supprimer.");
            return;
        }

        Materiels materiel = (Materiels) selectedRadio.getUserData();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Voulez-vous vraiment supprimer ce matériel ?");
        confirm.setContentText(materiel.getNom());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ServiceMateriel sm = new ServiceMateriel();
                sm.supprimer(materiel);
                materiels.remove(materiel);
                afficherMateriels();
                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "Le matériel a été supprimé.");
            }
        });
    }

    @FXML
    void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil - Gestion des Ressources");
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void filtrerMateriels(String motCle) {
        containerMateriels.getChildren().clear();

        for (Materiels m : materiels) {
            if (m.getNom().toLowerCase().contains(motCle.toLowerCase())) {
                VBox box = new VBox(8);
                box.setStyle("-fx-border-color: #ccc; -fx-background-color: white; -fx-padding: 10; -fx-alignment: center;");
                box.setPrefWidth(150);

                ImageView imageView = new ImageView();
                File file = new File(m.getImage() != null ? m.getImage() : "");
                imageView.setImage((file.exists()) ? new Image(file.toURI().toString()) : new Image("/images/nondispo.jpg"));
                imageView.setFitWidth(120);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);

                Label nomLabel = new Label(m.getNom());
                nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setUserData(m);

                box.getChildren().addAll(imageView, nomLabel, radioButton);
                containerMateriels.getChildren().add(box);
            }
        }
    }
    @FXML
    void filtrerMateriels() {
        String statut = cbFiltreStatut.getValue();
        String type = cbFiltreType.getValue();
        String quantiteStr = tfFiltreQuantite.getText().trim();

        List<Materiels> tous = new ServiceMateriel().recuperer();
        ObservableList<Materiels> filtres = FXCollections.observableArrayList();

        for (Materiels m : tous) {
            boolean match = true;

            if (!"Tous".equals(statut) && !m.getEtat().equalsIgnoreCase(statut)) match = false;
            if (!"Tous".equals(type) && !m.getType().equalsIgnoreCase(type)) match = false;
            if (!quantiteStr.isEmpty()) {
                try {
                    int q = Integer.parseInt(quantiteStr);
                    if (m.getQuantite() < q) match = false;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer un nombre.");
                    return;
                }
            }

            if (match) filtres.add(m);
        }

        materiels.setAll(filtres);
        afficherMateriels();
    }

}
