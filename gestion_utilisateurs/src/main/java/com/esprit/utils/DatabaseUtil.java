package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_utilisateurs";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            createTablesIfNotExist(conn);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé", e);
        }
    }

    private static void createTablesIfNotExist(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Désactiver temporairement les contraintes de clé étrangère
            stmt.execute("SET FOREIGN_KEY_CHECKS=0");

            // Table des rôles
            stmt.execute("CREATE TABLE IF NOT EXISTS `roles` (" +
                    "`id` INT PRIMARY KEY AUTO_INCREMENT, " +
                    "`name` VARCHAR(50) NOT NULL UNIQUE) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

            // Table des utilisateurs
            stmt.execute("CREATE TABLE IF NOT EXISTS `users` (" +
                    "`id` INT PRIMARY KEY AUTO_INCREMENT, " +
                    "`username` VARCHAR(100) NOT NULL, " +
                    "`password` VARCHAR(255) NOT NULL, " +
                    "`email` VARCHAR(100) NOT NULL UNIQUE, " +
                    "`role_id` INT NOT NULL, " +
                    "FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

            // Table pour les clients
            stmt.execute("CREATE TABLE IF NOT EXISTS `clients` (" +
                    "`user_id` INT PRIMARY KEY, " +
                    "`adresse` VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

            // Table pour les fournisseurs
            stmt.execute("CREATE TABLE IF NOT EXISTS `fournisseurs` (" +
                    "`user_id` INT PRIMARY KEY, " +
                    "`nom_entreprise` VARCHAR(100) NOT NULL, " +
                    "FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

            // Table pour les organisateurs
            stmt.execute("CREATE TABLE IF NOT EXISTS `organisateurs` (" +
                    "`user_id` INT PRIMARY KEY, " +
                    "`nom_organisation` VARCHAR(100) NOT NULL, " +
                    "FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

            // Insertion des rôles de base s'ils n'existent pas
            stmt.execute("INSERT IGNORE INTO `roles` (`id`, `name`) VALUES " +
                    "(1, 'admin'), (2, 'client'), (3, 'moderateur'), " +
                    "(4, 'fournisseur'), (5, 'organisateur')");

            // Réactiver les contraintes de clé étrangère
            stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        }
    }
}