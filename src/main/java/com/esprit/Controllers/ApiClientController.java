package com.esprit.Controllers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class ApiClientController {

    // Méthode pour récupérer les données depuis l'API
    public static String fetchDataFromApi() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://localhost:8000/api/location");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000); // Timeout 3 sec
            conn.setReadTimeout(3000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Code HTTP " + responseCode);
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                return in.lines().collect(Collectors.joining());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion à l'API :");
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
