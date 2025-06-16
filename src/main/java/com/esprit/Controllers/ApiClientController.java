package com.esprit.controllers;

import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ApiClientController {
    public static String fetchDataFromApi() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://localhost:8000/api/location");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000); // 3 secondes max
            conn.setReadTimeout(3000);

            // Vérification du code de réponse
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Code HTTP " + responseCode);
            }

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                return in.lines().collect(Collectors.joining());
            }
        } catch (Exception e) {
            // Journalisation détaillée
            System.err.println("Erreur de connexion à l'API:");
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public class SimpleApiTest {
        public static void main(String[] args) throws IOException {
            // Démarrer un serveur de test
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/test", exchange -> {
                String response = "{\"status\":\"OK\"}";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            });
            server.start();
            System.out.println("Serveur test démarré sur http://localhost:9000/test");
        }
    }
}