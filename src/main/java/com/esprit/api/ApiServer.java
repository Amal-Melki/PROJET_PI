package com.esprit.api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ApiServer {
    private static HttpServer server;

    public static void start() throws IOException {
        // Arrêter le serveur s'il est déjà en cours d'exécution
        stop();

        // Créer le serveur sur le port 8000
        server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Configurer le endpoint
        server.createContext("/api/location", exchange -> {
            System.out.println("Requête reçue de: " + exchange.getRemoteAddress());
            try {
                String response = "{\"ville\":\"Tunis\", \"lat\":36.8, \"lng\":10.1}";

                // Ajouter les headers CORS pour permettre les requêtes depuis JavaFX
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json");

                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                System.out.println(" Réponse envoyée");
            } catch (Exception e) {
                System.err.println(" Erreur dans le handler:");
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        });

        server.setExecutor(null); // Utilise l'exécuteur par défaut
        server.start();
        System.out.println(" API démarrée sur http://localhost:9000");
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println(" API arrêtée");
        }
    }
}
