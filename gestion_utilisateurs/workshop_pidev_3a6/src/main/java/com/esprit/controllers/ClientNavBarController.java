package com.esprit.controllers;

import com.esprit.models.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

public class ClientNavBarController {
    @FXML
    private Button btnAccueil;

    @FXML
    private Button btnEvenements;

    @FXML
    private ImageView imgClient;

    @FXML
    private Label lblClientName;

    private Client currentClient;

    public void setClient(Client client) {
        this.currentClient = client;
        updateClientInfo();
    }

    private void updateClientInfo() {
        if (currentClient != null) {
            // Set client name
            lblClientName.setText(currentClient.getNom_suser() + " " + currentClient.getPrenom_user());

            // Set client image
            try {
                String imagePath = currentClient.getImage_path();
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(getClass().getResourceAsStream("/" + imagePath));
                    imgClient.setImage(image);
                }
            } catch (Exception e) {
                System.out.println("Error loading client image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Evenements.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnEvenements.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 