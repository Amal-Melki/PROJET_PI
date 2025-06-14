package com.esprit.services;

import com.esprit.exceptions.ServiceException;
import com.esprit.models.models.Admin;
import com.esprit.models.models.Admin;
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
        if (admin == null) throw new ServiceException("Admin ne peut être null");
        if (admin.getEmail() == null || admin.getEmail().isEmpty()) 
            throw new ServiceException("Email requis");

        String checkEmail = "SELECT id_user FROM user WHERE email_user=?";
        String reqUser = "INSERT INTO user (nom_suser, prenom_user, email_user, password_user) VALUES (?,?,?,?)";
        String reqAdmin = "INSERT INTO admin (role, id_user) VALUES (?,?)";

        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstCheck = connection.prepareStatement(checkEmail)) {
                pstCheck.setString(1, admin.getEmail());
                if (pstCheck.executeQuery().next()) {
                    throw new ServiceException("Email déjà utilisé");
                }
            }

            try (PreparedStatement pstUser = connection.prepareStatement(reqUser, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstAdmin = connection.prepareStatement(reqAdmin)) {
                
                pstUser.setString(1, admin.getNom_suser());
                pstUser.setString(2, admin.getPrenom_user());
                pstUser.setString(3, admin.getEmail_user());
                pstUser.setString(4, admin.getPassword_user());
                pstUser.executeUpdate();

                try (ResultSet rs = pstUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        pstAdmin.setString(1, admin.getRole());
                        pstAdmin.setInt(2, rs.getInt(1));
                        pstAdmin.executeUpdate();
                        connection.commit();
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new ServiceException("Rollback échoué", ex);
            }
            throw new ServiceException("Échec création admin", e);
        } finally {
            resetAutoCommit();
        }
    }

    @Override
    public void modifier(Admin admin) {
        String reqUser = "UPDATE user SET nom_suser=?, prenom_user=?, email_user=?, password_user=? WHERE id_user=?";
        String reqAdmin = "UPDATE admin SET role=? WHERE id_admin=?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstUser = connection.prepareStatement(reqUser);
                 PreparedStatement pstAdmin = connection.prepareStatement(reqAdmin)) {
                
                pstUser.setString(1, admin.getNom_suser());
                pstUser.setString(2, admin.getPrenom_user());
                pstUser.setString(3, admin.getEmail_user());
                pstUser.setString(4, admin.getPassword_user());
                pstUser.setInt(5, admin.getId_user());
                pstUser.executeUpdate();
                
                pstAdmin.setString(1, admin.getRole());
                pstAdmin.setInt(2, admin.getId_admin());
                pstAdmin.executeUpdate();
                
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new ServiceException("Échec modification admin", e);
            }
        } catch (SQLException e) {
            throw new ServiceException("Erreur transaction", e);
        }
    }

    @Override
    public void supprimer(Admin admin) {
        String reqAdmin = "DELETE FROM admin WHERE id_admin=?";
        String reqUser = "DELETE FROM user WHERE id_user=?";
        
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement pstAdmin = connection.prepareStatement(reqAdmin);
                 PreparedStatement pstUser = connection.prepareStatement(reqUser)) {
                
                pstAdmin.setInt(1, admin.getId_admin());
                pstAdmin.executeUpdate();
                
                pstUser.setInt(1, admin.getId_user());
                pstUser.executeUpdate();
                
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new ServiceException("Échec suppression admin", e);
            }
        } catch (SQLException e) {
            throw new ServiceException("Erreur transaction", e);
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

    public Admin findByEmail(String email) {
        String query = "SELECT a.*, u.* FROM admin a JOIN user u ON a.id_user = u.id_user WHERE u.email_user = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
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

    private void resetAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Warning: Échec reset autocommit");
        }
    }
}