package com.esprit.tests;

import com.esprit.modules.utilisateurs.Role;
import com.esprit.modules.utilisateurs.User;
import com.esprit.modules.utilisateurs.Client;
import com.esprit.modules.utilisateurs.Admin;
import com.esprit.modules.utilisateurs.Moderateur;
import com.esprit.services.UserDAO;
import com.esprit.services.UserDAOImpl;
import com.esprit.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            UserDAO userDAO = new UserDAOImpl(connection);

            // Créer les rôles
            Role roleAdmin = new Role(1, "admin");
            Role roleClient = new Role(2, "client");
            Role roleModerateur = new Role(3, "modérateur");

            // Créer un administrateur (Draven)
            User admin = new Admin(1, "Draven", "admin123", "draven@example.com", roleAdmin);
            userDAO.create(admin);
            System.out.println("Admin créé : " + admin);

            // Créer un client (Kiwi)
            User client = new Client(2, "Kiwi", "client123", "kiwi@example.com", roleClient, "456 Avenue des Champs");
            userDAO.create(client);
            System.out.println("Client créé : " + client);

            // Créer un modérateur (Mimi)
            User moderateur = new Moderateur(3, "Mimi", "moderateur123", "mimi@example.com", roleModerateur);
            userDAO.create(moderateur);
            System.out.println("Modérateur créé : " + moderateur);

            // Lire les utilisateurs pour vérifier
            System.out.println("\nUtilisateurs dans la base de données :");
            System.out.println(userDAO.read(1)); // Draven
            System.out.println(userDAO.read(2)); // Kiwi
            System.out.println(userDAO.read(3)); // Mimi
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}