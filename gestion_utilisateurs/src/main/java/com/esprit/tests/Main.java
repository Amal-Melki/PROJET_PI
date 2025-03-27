package com.esprit.tests;

import com.esprit.modules.utilisateurs.*;
import com.esprit.services.UserDAO;
import com.esprit.services.UserDAOImpl;
import com.esprit.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            System.out.println("Connexion à la base de données réussie.");

            UserDAO userDAO = new UserDAOImpl(connection);
            Scanner scanner = new Scanner(System.in);

            // Création des rôles
            Role roleAdmin = new Role(1, "admin");
            Role roleClient = new Role(2, "client");
            Role roleModerateur = new Role(3, "moderateur");
            Role roleFournisseur = new Role(4, "fournisseur");
            Role roleOrganisateur = new Role(5, "organisateur");

            boolean continuer = true;

            while (continuer) {
                System.out.println("\n--- Menu Principal ---");
                System.out.println("1. Gestion des Utilisateurs");
                System.out.println("2. Authentification");
                System.out.println("3. Statistiques");
                System.out.println("4. Quitter");
                System.out.print("Choix : ");
                int mainChoice = scanner.nextInt();
                scanner.nextLine();

                switch (mainChoice) {
                    case 1:
                        userManagementMenu(userDAO, scanner, roleAdmin, roleClient,
                                roleModerateur, roleFournisseur, roleOrganisateur);
                        break;
                    case 2:
                        authenticationMenu(userDAO, scanner);
                        break;
                    case 3:
                        statsMenu(userDAO, scanner);
                        break;
                    case 4:
                        continuer = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Option invalide.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void userManagementMenu(UserDAO userDAO, Scanner scanner,
                                           Role roleAdmin, Role roleClient,
                                           Role roleModerateur, Role roleFournisseur,
                                           Role roleOrganisateur) throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Utilisateurs ---");
            System.out.println("1. Ajouter un utilisateur");
            System.out.println("2. Lister tous les utilisateurs");
            System.out.println("3. Rechercher un utilisateur");
            System.out.println("4. Modifier un utilisateur");
            System.out.println("5. Supprimer un utilisateur");
            System.out.println("6. Changer mot de passe");
            System.out.println("7. Retour");
            System.out.print("Choix : ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addUser(userDAO, scanner, roleAdmin, roleClient,
                            roleModerateur, roleFournisseur, roleOrganisateur);
                    break;
                case 2:
                    listAllUsers(userDAO);
                    break;
                case 3:
                    searchUser(userDAO, scanner);
                    break;
                case 4:
                    updateUser(userDAO, scanner);
                    break;
                case 5:
                    deleteUser(userDAO, scanner);
                    break;
                case 6:
                    changePasswordMenu(userDAO, scanner);
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Option invalide.");
            }
        }
    }

    private static void authenticationMenu(UserDAO userDAO, Scanner scanner) throws SQLException {
        System.out.println("\n--- Authentification ---");
        System.out.print("Nom d'utilisateur : ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        User user = userDAO.login(username, password);
        if (user != null) {
            System.out.println("Authentification réussie !");
            System.out.println("Bienvenue " + user.getUsername() + " (" + user.getRole().getName() + ")");
            user.afficherDetails();
        } else {
            System.out.println("Échec de l'authentification. Vérifiez vos identifiants.");
        }
    }

    private static void addUser(UserDAO userDAO, Scanner scanner,
                                Role roleAdmin, Role roleClient,
                                Role roleModerateur, Role roleFournisseur,
                                Role roleOrganisateur) throws SQLException {
        System.out.println("\n--- Ajout d'un utilisateur ---");
        System.out.print("Type (admin/client/moderateur/fournisseur/organisateur) : ");
        String type = scanner.nextLine().toLowerCase();
        System.out.print("Username : ");
        String username = scanner.nextLine();
        System.out.print("Password : ");
        String password = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();

        User user = null;
        switch (type) {
            case "admin":
                user = new Admin(0, username, password, email, roleAdmin);
                break;
            case "client":
                System.out.print("Adresse : ");
                String adresse = scanner.nextLine();
                user = new Client(0, username, password, email, roleClient, adresse);
                break;
            case "moderateur":
                user = new Moderateur(0, username, password, email, roleModerateur);
                break;
            case "fournisseur":
                System.out.print("Nom entreprise : ");
                String entreprise = scanner.nextLine();
                user = new Fournisseur(0, username, password, email, roleFournisseur, entreprise);
                break;
            case "organisateur":
                System.out.print("Nom organisation : ");
                String org = scanner.nextLine();
                user = new Organisateur(0, username, password, email, roleOrganisateur, org);
                break;
            default:
                System.out.println("Type invalide.");
                return;
        }

        userDAO.ajouterUtilisateur(user);
        System.out.println("Utilisateur ajouté avec succès ! ID : " + user.getId());
    }

    private static void listAllUsers(UserDAO userDAO) throws SQLException {
        System.out.println("\n--- Liste des utilisateurs ---");
        List<User> users = userDAO.getTousLesUtilisateurs();
        if (users.isEmpty()) {
            System.out.println("Aucun utilisateur trouvé.");
        } else {
            for (User user : users) {
                System.out.println("--------------------------------");
                System.out.println(user);
                user.afficherDetails();
            }
            System.out.println("--------------------------------");
            System.out.println("Total: " + users.size() + " utilisateurs");
        }
    }

    private static void searchUser(UserDAO userDAO, Scanner scanner) throws SQLException {
        System.out.println("\n--- Recherche d'utilisateur ---");
        System.out.println("1. Par ID");
        System.out.println("2. Par nom");
        System.out.println("3. Par email");
        System.out.print("Choix : ");
        int searchType = scanner.nextInt();
        scanner.nextLine();

        switch (searchType) {
            case 1:
                System.out.print("ID : ");
                int id = scanner.nextInt();
                scanner.nextLine();
                User user = userDAO.getUtilisateurParId(id);
                if (user != null) {
                    System.out.println("\nUtilisateur trouvé :");
                    System.out.println(user);
                    user.afficherDetails();
                } else {
                    System.out.println("Utilisateur non trouvé.");
                }
                break;
            case 2:
                System.out.print("Nom : ");
                String name = scanner.nextLine();
                List<User> users = userDAO.rechercherParNom(name);
                if (users.isEmpty()) {
                    System.out.println("Aucun utilisateur trouvé.");
                } else {
                    System.out.println("\nRésultats de la recherche :");
                    for (User u : users) {
                        System.out.println("--------------------------------");
                        System.out.println(u);
                        u.afficherDetails();
                    }
                    System.out.println("--------------------------------");
                    System.out.println("Total: " + users.size() + " utilisateurs trouvés");
                }
                break;
            case 3:
                System.out.print("Email : ");
                String email = scanner.nextLine();
                User userByEmail = userDAO.rechercherParEmail(email);
                if (userByEmail != null) {
                    System.out.println("\nUtilisateur trouvé :");
                    System.out.println(userByEmail);
                    userByEmail.afficherDetails();
                } else {
                    System.out.println("Utilisateur non trouvé.");
                }
                break;
            default:
                System.out.println("Option invalide.");
        }
    }

    private static void updateUser(UserDAO userDAO, Scanner scanner) throws SQLException {
        System.out.println("\n--- Modification d'utilisateur ---");
        System.out.print("ID de l'utilisateur à modifier : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        User user = userDAO.getUtilisateurParId(id);
        if (user == null) {
            System.out.println("Utilisateur non trouvé.");
            return;
        }

        System.out.println("\nAnciennes valeurs :");
        System.out.println(user);
        user.afficherDetails();

        System.out.print("\nNouveau username (laisser vide pour ne pas changer) : ");
        String username = scanner.nextLine();
        if (!username.isEmpty()) user.setUsername(username);

        System.out.print("Nouveau password (laisser vide pour ne pas changer) : ");
        String password = scanner.nextLine();
        if (!password.isEmpty()) user.setPassword(password);

        System.out.print("Nouvel email (laisser vide pour ne pas changer) : ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) user.setEmail(email);

        // Gestion des attributs spécifiques
        if (user instanceof Client) {
            System.out.print("Nouvelle adresse (laisser vide pour ne pas changer) : ");
            String adresse = scanner.nextLine();
            if (!adresse.isEmpty()) ((Client) user).setAdresse(adresse);
        } else if (user instanceof Fournisseur) {
            System.out.print("Nouveau nom d'entreprise (laisser vide pour ne pas changer) : ");
            String entreprise = scanner.nextLine();
            if (!entreprise.isEmpty()) ((Fournisseur) user).setNomEntreprise(entreprise);
        } else if (user instanceof Organisateur) {
            System.out.print("Nouveau nom d'organisation (laisser vide pour ne pas changer) : ");
            String org = scanner.nextLine();
            if (!org.isEmpty()) ((Organisateur) user).setNomOrganisation(org);
        }

        userDAO.modifierUtilisateur(user);
        System.out.println("\nUtilisateur modifié avec succès !");
        System.out.println("Nouvelles valeurs :");
        System.out.println(user);
        user.afficherDetails();
    }

    private static void deleteUser(UserDAO userDAO, Scanner scanner) throws SQLException {
        System.out.println("\n--- Suppression d'utilisateur ---");
        System.out.print("ID de l'utilisateur à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        User user = userDAO.getUtilisateurParId(id);
        if (user == null) {
            System.out.println("Utilisateur non trouvé.");
            return;
        }

        System.out.println("\nVous êtes sur le point de supprimer :");
        System.out.println(user);
        user.afficherDetails();
        System.out.print("\nConfirmer la suppression (o/n) ? ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            userDAO.supprimerUtilisateur(id);
            System.out.println("Utilisateur supprimé avec succès !");
        } else {
            System.out.println("Suppression annulée.");
        }
    }

    private static void changePasswordMenu(UserDAO userDAO, Scanner scanner) throws SQLException {
        System.out.println("\n--- Changement de mot de passe ---");
        System.out.print("ID de l'utilisateur : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        User user = userDAO.getUtilisateurParId(id);
        if (user == null) {
            System.out.println("Utilisateur non trouvé.");
            return;
        }

        System.out.print("Nouveau mot de passe : ");
        String newPassword = scanner.nextLine();

        userDAO.changePassword(user, newPassword);
        System.out.println("Mot de passe changé avec succès !");
    }

    private static void statsMenu(UserDAO userDAO, Scanner scanner) throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Statistiques ---");
            System.out.println("1. Nombre total d'utilisateurs");
            System.out.println("2. Nombre d'utilisateurs par rôle");
            System.out.println("3. Liste des utilisateurs par rôle");
            System.out.println("4. Retour");
            System.out.print("Choix : ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nNombre total d'utilisateurs : " +
                            userDAO.getNombreTotalUtilisateurs());
                    break;
                case 2:
                    System.out.print("\nRôle à compter (admin/client/moderateur/fournisseur/organisateur) : ");
                    String role = scanner.nextLine();
                    int count = userDAO.compterUtilisateursParRole(role);
                    System.out.println("Nombre d'utilisateurs avec le rôle '" + role + "' : " + count);
                    break;
                case 3:
                    System.out.print("\nRôle à lister (admin/client/moderateur/fournisseur/organisateur) : ");
                    String roleName = scanner.nextLine();
                    List<User> users = userDAO.getUtilisateursParRole(roleName);
                    if (users.isEmpty()) {
                        System.out.println("Aucun utilisateur trouvé pour ce rôle.");
                    } else {
                        System.out.println("\nUtilisateurs avec le rôle '" + roleName + "' :");
                        for (User user : users) {
                            System.out.println("--------------------------------");
                            System.out.println(user);
                            user.afficherDetails();
                        }
                        System.out.println("--------------------------------");
                        System.out.println("Total: " + users.size() + " utilisateurs");
                    }
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Option invalide.");
            }
        }
    }
}