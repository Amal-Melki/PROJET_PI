package com.esprit.services;

import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(User user) {
        String req = "INSERT INTO user (nom_suser, prenom_user, email_user, password_user) VALUES (?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, user.getNom_suser());
            pst.setString(2, user.getPrenom_user());
            pst.setString(3, user.getEmail_user());
            pst.setString(4, user.getPassword_user());
            pst.executeUpdate();
            System.out.println("User ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(User user) {
        String req = "UPDATE user SET nom_suser=?, prenom_user=?, email_user=?, password_user=? WHERE id_user=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, user.getNom_suser());
            pst.setString(2, user.getPrenom_user());
            pst.setString(3, user.getEmail_user());
            pst.setString(4, user.getPassword_user());
            pst.setInt(5, user.getId_user());
            pst.executeUpdate();
            System.out.println("User modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(User user) {
        String req = "DELETE FROM user WHERE id_user=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, user.getId_user());
            pst.executeUpdate();
            System.out.println("User supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<User> rechercher() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                users.add(new User(
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
        return users;
    }
} 