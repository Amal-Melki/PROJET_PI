package com.esprit.services;

import com.esprit.models.Client;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService implements IService<Client> {
    private Connection connection;

    public ClientService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Client client) {
        // First add the user
        String reqUser = "INSERT INTO user (nom_suser, prenom_user, email_user, password_user) VALUES (?,?,?,?)";
        String reqClient = "INSERT INTO client (numero_tel, image_path, id_user) VALUES (?,?,?)";
        try {
            // Insert into user table
            PreparedStatement pstUser = connection.prepareStatement(reqUser, Statement.RETURN_GENERATED_KEYS);
            pstUser.setString(1, client.getNom_suser());
            pstUser.setString(2, client.getPrenom_user());
            pstUser.setString(3, client.getEmail_user());
            pstUser.setString(4, client.getPassword_user());
            pstUser.executeUpdate();

            // Get the generated user id
            ResultSet generatedKeys = pstUser.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                
                // Insert into client table
                PreparedStatement pstClient = connection.prepareStatement(reqClient);
                pstClient.setInt(1, client.getNumero_tel());
                pstClient.setString(2, client.getImage_path());
                pstClient.setInt(3, userId);
                pstClient.executeUpdate();
            }
            System.out.println("Client ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Client client) {
        String reqUser = "UPDATE user SET nom_suser=?, prenom_user=?, email_user=?, password_user=? WHERE id_user=?";
        String reqClient = "UPDATE client SET numero_tel=?, image_path=? WHERE id_client=?";
        try {
            // Update user table
            PreparedStatement pstUser = connection.prepareStatement(reqUser);
            pstUser.setString(1, client.getNom_suser());
            pstUser.setString(2, client.getPrenom_user());
            pstUser.setString(3, client.getEmail_user());
            pstUser.setString(4, client.getPassword_user());
            pstUser.setInt(5, client.getId_user());
            pstUser.executeUpdate();

            // Update client table
            PreparedStatement pstClient = connection.prepareStatement(reqClient);
            pstClient.setInt(1, client.getNumero_tel());
            pstClient.setString(2, client.getImage_path());
            pstClient.setInt(3, client.getId_client());
            pstClient.executeUpdate();
            
            System.out.println("Client modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Client client) {
        String reqClient = "DELETE FROM client WHERE id_client=?";
        String reqUser = "DELETE FROM user WHERE id_user=?";
        try {
            // First delete from client table
            PreparedStatement pstClient = connection.prepareStatement(reqClient);
            pstClient.setInt(1, client.getId_client());
            pstClient.executeUpdate();

            // Then delete from user table
            PreparedStatement pstUser = connection.prepareStatement(reqUser);
            pstUser.setInt(1, client.getId_user());
            pstUser.executeUpdate();
            
            System.out.println("Client supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Client> rechercher() {
        List<Client> clients = new ArrayList<>();
        String req = "SELECT c.*, u.* FROM client c JOIN user u ON c.id_user = u.id_user";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                clients.add(new Client(
                    rs.getInt("id_client"),
                    rs.getInt("numero_tel"),
                    rs.getString("image_path"),
                    rs.getInt("id_user"),
                    rs.getString("nom_suser"),
                    rs.getString("prenom_user"),
                    rs.getString("email_user"),
                    rs.getString("password_user")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return clients;
    }

    public Client authenticate(String email, String password) {
        String query = "SELECT c.*, u.nom_suser, u.prenom_user, u.email_user, u.password_user " +
                      "FROM client c " +
                      "JOIN user u ON c.id_user = u.id_user " +
                      "WHERE u.email_user = ? AND u.password_user = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getInt("id_client"),
                    rs.getInt("numero_tel"),
                    rs.getString("image_path"),
                    rs.getInt("id_user"),
                    rs.getString("nom_suser"),
                    rs.getString("prenom_user"),
                    rs.getString("email_user"),
                    rs.getString("password_user")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client rechercherParId(int userId) {
        String req = "SELECT c.*, u.* FROM client c JOIN user u ON c.id_user = u.id_user WHERE u.id_user = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getInt("id_client"),
                    rs.getInt("numero_tel"),
                    rs.getString("image_path"),
                    rs.getInt("id_user"),
                    rs.getString("nom_suser"),
                    rs.getString("prenom_user"),
                    rs.getString("email_user"),
                    rs.getString("password_user")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
} 