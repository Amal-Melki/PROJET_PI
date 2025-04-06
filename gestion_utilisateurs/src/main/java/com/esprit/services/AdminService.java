package com.esprit.services;

import com.esprit.models.Admin;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService implements IService<Admin> {
    private Connection connection;

    public AdminService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Admin admin) {
        // First add the user
        String reqUser = "INSERT INTO user (nom_suser, prenom_user, email_user, password_user) VALUES (?,?,?,?)";
        String reqAdmin = "INSERT INTO admin (role, id_user) VALUES (?,?)";
        try {
            // Insert into user table
            PreparedStatement pstUser = connection.prepareStatement(reqUser, Statement.RETURN_GENERATED_KEYS);
            pstUser.setString(1, admin.getNom_suser());
            pstUser.setString(2, admin.getPrenom_user());
            pstUser.setString(3, admin.getEmail_user());
            pstUser.setString(4, admin.getPassword_user());
            pstUser.executeUpdate();

            // Get the generated user id
            ResultSet generatedKeys = pstUser.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                
                // Insert into admin table
                PreparedStatement pstAdmin = connection.prepareStatement(reqAdmin);
                pstAdmin.setInt(1, admin.getRole());
                pstAdmin.setInt(2, userId);
                pstAdmin.executeUpdate();
            }
            System.out.println("Admin ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Admin admin) {
        String reqUser = "UPDATE user SET nom_suser=?, prenom_user=?, email_user=?, password_user=? WHERE id_user=?";
        String reqAdmin = "UPDATE admin SET role=? WHERE id_admin=?";
        try {
            // Update user table
            PreparedStatement pstUser = connection.prepareStatement(reqUser);
            pstUser.setString(1, admin.getNom_suser());
            pstUser.setString(2, admin.getPrenom_user());
            pstUser.setString(3, admin.getEmail_user());
            pstUser.setString(4, admin.getPassword_user());
            pstUser.setInt(5, admin.getId_user());
            pstUser.executeUpdate();

            // Update admin table
            PreparedStatement pstAdmin = connection.prepareStatement(reqAdmin);
            pstAdmin.setInt(1, admin.getRole());
            pstAdmin.setInt(2, admin.getId_admin());
            pstAdmin.executeUpdate();
            
            System.out.println("Admin modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Admin admin) {
        String reqAdmin = "DELETE FROM admin WHERE id_admin=?";
        String reqUser = "DELETE FROM user WHERE id_user=?";
        try {
            // First delete from admin table
            PreparedStatement pstAdmin = connection.prepareStatement(reqAdmin);
            pstAdmin.setInt(1, admin.getId_admin());
            pstAdmin.executeUpdate();

            // Then delete from user table
            PreparedStatement pstUser = connection.prepareStatement(reqUser);
            pstUser.setInt(1, admin.getId_user());
            pstUser.executeUpdate();
            
            System.out.println("Admin supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Admin> rechercher() {
        List<Admin> admins = new ArrayList<>();
        String req = "SELECT a.*, u.* FROM admin a JOIN user u ON a.id_user = u.id_user";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                admins.add(new Admin(
                    rs.getInt("id_admin"),
                    rs.getInt("role"),
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
        return admins;
    }

    public Admin authenticate(String email, String password) {
        String query = "SELECT a.*, u.* FROM admin a " +
                      "JOIN user u ON a.id_user = u.id_user " +
                      "WHERE u.email_user = ? AND u.password_user = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getInt("id_admin"),
                    rs.getInt("role"),
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
} 