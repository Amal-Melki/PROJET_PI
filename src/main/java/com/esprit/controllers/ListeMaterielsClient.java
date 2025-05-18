package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListeMaterielsClient implements Initializable {

    @FXML
    private TilePane tilePane;
    @FXML
    private Button btnRetourAccueil;
    @FXML
    private TextField tfRecherche;
    @FXML
    private ComboBox<String> cbFiltreStatut;
    @FXML
    private ComboBox<String> cbFiltreType;
    @FXML
    private TextField tfFiltreQuantite;

    private final ObservableList<Materiels> tousMateriels = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceMateriel sm = new ServiceMateriel();
        tousMateriels.setAll(sm.recuperer().stream()
                .filter(m -> m.getQuantite() > 0 && "DISPONIBLE".equalsIgnoreCase(m.getEtat()))
                .collect(Collectors.toList()));

        cbFiltreStatut.setItems(FXCollections.observableArrayList("Tous", "DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE"));
        cbFiltreType.setItems(FXCollections.observableArrayList("Tous", "Mobilier", "Éclairage", "Sonorisation", "Audiovisuel"));
        cbFiltreStatut.setValue("Tous");
        cbFiltreType.setValue("Tous");

        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> appliquerFiltres());
        cbFiltreStatut.setOnAction(e -> appliquerFiltres());
        cbFiltreType.setOnAction(e -> appliquerFiltres());
        tfFiltreQuantite.textProperty().addListener((obs, oldVal, newVal) -> appliquerFiltres());

        afficherMateriels(tousMateriels);
    }

    private void afficherMateriels(List<Materiels> liste) {
        tilePane.getChildren().clear();
        for (Materiels m : liste) {
            VBox vbox = creerCarteMateriel(m);
            tilePane.getChildren().add(vbox);
        }
    }

    private VBox creerCarteMateriel(Materiels materiel) {
        VBox box = new VBox(5);
        box.setPrefWidth(160);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        box.setPadding(new Insets(10));

        ImageView imageView = new ImageView();
        if (materiel.getImage() != null && !materiel.getImage().trim().isEmpty()) {
            File file = new File(materiel.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            } else {
                imageView.setImage(new Image("/images/nondispo.jpg"));
            }
        } else {
            imageView.setImage(new Image("/images/nondispo.jpg"));
        }

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Label nomLabel = new Label(materiel.getNom());
        nomLabel.setFont(new Font("Arial", 14));
        nomLabel.setWrapText(true);

        Label quantiteLabel = new Label("Quantité : " + materiel.getQuantite());
        Label prixLabel = new Label("Prix : " + materiel.getPrix() + " TND");

        Button btnReserver = new Button("Réserver");
        btnReserver.setStyle("-fx-background-color: #ff7fa8; -fx-text-fill: white;");
        btnReserver.setOnAction(e -> ouvrirFormulaireReservation(materiel));

        box.getChildren().addAll(imageView, nomLabel, quantiteLabel, prixLabel, btnReserver);
        return box;
    }

    @FXML
    private void appliquerFiltres() {
        String recherche = tfRecherche.getText().toLowerCase().trim();
        String statut = cbFiltreStatut.getValue();
        String type = cbFiltreType.getValue();
        String qStr = tfFiltreQuantite.getText().trim();

        List<Materiels> filtres = tousMateriels.stream()
                .filter(m -> m.getNom().toLowerCase().contains(recherche))
                .filter(m -> "Tous".equals(statut) || m.getEtat().equalsIgnoreCase(statut))
                .filter(m -> "Tous".equals(type) || m.getType().equalsIgnoreCase(type))
                .filter(m -> {
                    if (qStr.isEmpty()) return true;
                    try {
                        return m.getQuantite() >= Integer.parseInt(qStr);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        afficherMateriels(filtres);
    }

    private void ouvrirFormulaireReservation(Materiels materiel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReservationClient.fxml"));
            Parent root = loader.load();

            AjoutReservationClient controller = loader.getController();
            controller.setMaterielSelectionne(materiel);

            Stage stage = new Stage();
            stage.setTitle("Réserver Matériel");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
