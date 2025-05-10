package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSource {
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());
    private static DataSource instance;
    private Connection connection;

    // Configuration de la base de données
    private final String URL = "jdbc:mysql://localhost:3306/gestion_produit_derive";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    private DataSource() {
        establishConnection();
    }

    public static synchronized DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    private void establishConnection() {
        int attempts = 0;
        SQLException lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                logger.info("Connexion à la base de données établie avec succès");
                return;
            } catch (SQLException e) {
                attempts++;
                lastException = e;
                logger.log(Level.SEVERE, "Échec de la connexion (tentative " + attempts + ")", e);

                if (attempts < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.log(Level.SEVERE, "Interruption pendant la reconnexion", ie);
                        throw new RuntimeException("Interruption pendant la reconnexion", ie);
                    }
                }
            }
        }
        throw new RuntimeException("Échec de la connexion après " + MAX_RETRIES + " tentatives", lastException);
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                establishConnection();
            }
            return connection;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur de vérification de la connexion", e);
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion", e);
        }
    }
}