package com.esprit.services;

import com.esprit.modules.utilisateurs.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAOImpl implements UserDAO {
    private Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hachage du mot de passe", e);
        }
    }

    @Override
    public void ajouterUtilisateur(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, email, role_id) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashPassword(user.getPassword()));
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, user.getRole().getId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));

                    if (user instanceof Client) {
                        ajouterClient((Client) user);
                    } else if (user instanceof Fournisseur) {
                        ajouterFournisseur((Fournisseur) user);
                    } else if (user instanceof Organisateur) {
                        ajouterOrganisateur((Organisateur) user);
                    }
                }
            }
        }
    }

    private void ajouterClient(Client client) throws SQLException {
        String sql = "INSERT INTO clients(user_id, adresse) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, client.getId());
            pstmt.setString(2, client.getAdresse());
            pstmt.executeUpdate();
        }
    }

    private void ajouterFournisseur(Fournisseur fournisseur) throws SQLException {
        String sql = "INSERT INTO fournisseurs(user_id, nom_entreprise) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, fournisseur.getId());
            pstmt.setString(2, fournisseur.getNomEntreprise());
            pstmt.executeUpdate();
        }
    }

    private void ajouterOrganisateur(Organisateur organisateur) throws SQLException {
        String sql = "INSERT INTO organisateurs(user_id, nom_organisation) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, organisateur.getId());
            pstmt.setString(2, organisateur.getNomOrganisation());
            pstmt.executeUpdate();
        }
    }

    @Override
    public User getUtilisateurParId(int id) throws SQLException {
        String sql = "SELECT u.*, r.name as role_name FROM users u JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getInt("role_id"), rs.getString("role_name"));
                    String userType = role.getName();

                    switch (userType) {
                        case "client":
                            return getClient(id, rs, role);
                        case "fournisseur":
                            return getFournisseur(id, rs, role);
                        case "organisateur":
                            return getOrganisateur(id, rs, role);
                        case "admin":
                            return new Admin(id, rs.getString("username"), "",
                                    rs.getString("email"), role);
                        case "moderateur":
                            return new Moderateur(id, rs.getString("username"), "",
                                    rs.getString("email"), role);
                        default:
                            return null;
                    }
                }
            }
        }
        return null;
    }

    private Client getClient(int id, ResultSet rs, Role role) throws SQLException {
        String sql = "SELECT adresse FROM clients WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet clientRs = pstmt.executeQuery()) {
                if (clientRs.next()) {
                    return new Client(id, rs.getString("username"), "",
                            rs.getString("email"), role, clientRs.getString("adresse"));
                }
            }
        }
        return null;
    }

    private Fournisseur getFournisseur(int id, ResultSet rs, Role role) throws SQLException {
        String sql = "SELECT nom_entreprise FROM fournisseurs WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet fournisseurRs = pstmt.executeQuery()) {
                if (fournisseurRs.next()) {
                    return new Fournisseur(id, rs.getString("username"), "",
                            rs.getString("email"), role, fournisseurRs.getString("nom_entreprise"));
                }
            }
        }
        return null;
    }

    private Organisateur getOrganisateur(int id, ResultSet rs, Role role) throws SQLException {
        String sql = "SELECT nom_organisation FROM organisateurs WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet organisateurRs = pstmt.executeQuery()) {
                if (organisateurRs.next()) {
                    return new Organisateur(id, rs.getString("username"), "",
                            rs.getString("email"), role, organisateurRs.getString("nom_organisation"));
                }
            }
        }
        return null;
    }

    @Override
    public void modifierUtilisateur(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, role_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getRole().getId());
            pstmt.setInt(4, user.getId());
            pstmt.executeUpdate();
        }

        if (user instanceof Client) {
            updateClient((Client) user);
        } else if (user instanceof Fournisseur) {
            updateFournisseur((Fournisseur) user);
        } else if (user instanceof Organisateur) {
            updateOrganisateur((Organisateur) user);
        }
    }

    private void updateClient(Client client) throws SQLException {
        String sql = "UPDATE clients SET adresse = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getAdresse());
            pstmt.setInt(2, client.getId());
            pstmt.executeUpdate();
        }
    }

    private void updateFournisseur(Fournisseur fournisseur) throws SQLException {
        String sql = "UPDATE fournisseurs SET nom_entreprise = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fournisseur.getNomEntreprise());
            pstmt.setInt(2, fournisseur.getId());
            pstmt.executeUpdate();
        }
    }

    private void updateOrganisateur(Organisateur organisateur) throws SQLException {
        String sql = "UPDATE organisateurs SET nom_organisation = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organisateur.getNomOrganisation());
            pstmt.setInt(2, organisateur.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void supprimerUtilisateur(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<User> getTousLesUtilisateurs() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id FROM users";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = getUtilisateurParId(rs.getInt("id"));
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    @Override
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String inputHash = hashPassword(password);

                    if (storedHash.equals(inputHash)) {
                        return getUtilisateurParId(rs.getInt("id"));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void changePassword(User user, String nouveauMotDePasse) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hashPassword(nouveauMotDePasse));
            pstmt.setInt(2, user.getId());
            pstmt.executeUpdate();
        }
        user.setPassword("");
    }

    @Override
    public List<User> rechercherParNom(String nom) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id FROM users WHERE username LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = getUtilisateurParId(rs.getInt("id"));
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        }
        return users;
    }

    @Override
    public User rechercherParEmail(String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return getUtilisateurParId(rs.getInt("id"));
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getUtilisateursParRole(String roleName) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id FROM users u JOIN roles r ON u.role_id = r.id WHERE r.name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = getUtilisateurParId(rs.getInt("id"));
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        }
        return users;
    }

    @Override
    public int compterUtilisateursParRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM users u JOIN roles r ON u.role_id = r.id WHERE r.name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    @Override
    public int getNombreTotalUtilisateurs() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM users";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
}