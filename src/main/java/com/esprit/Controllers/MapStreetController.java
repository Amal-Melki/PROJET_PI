package com.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MapStreetController {

    @FXML
    private WebView webView;
    @FXML
    private TextField addressField;

    private static final String DEFAULT_LOCATION = "Tunis, ben arous";
    private static final String OSM_URL = "https://www.openstreetmap.org/#map=8/35.159/4.944";

    @FXML
    public void initialize() {
        loadMap(DEFAULT_LOCATION);     }

    @FXML
    private void handleSearch() {
        String address = addressField.getText().trim();
        if (!address.isEmpty()) {
            loadMap(address);
        } else {
            loadMap(DEFAULT_LOCATION);
        }
    }

    private void loadMap(String location) {
        try {
            String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
            String fullUrl = OSM_URL + encodedLocation;
            webView.getEngine().load(fullUrl);
        } catch (Exception e) {
            e.printStackTrace();
            webView.getEngine().load("https://www.openstreetmap.org");
        }
    }

    // Méthode pour charger la carte avec des coordonnées spécifiques
    public void setLocation(double latitude, double longitude) {
        String url = "https://www.openstreetmap.org/?mlat=" + latitude +
                "&mlon=" + longitude +
                "#map=17/" + latitude + "/" + longitude;
        webView.getEngine().load(url);
    }
}