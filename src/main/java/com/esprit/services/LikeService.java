package com.esprit.services;

import com.esprit.modules.Like;
import com.esprit.modules.User;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LikeService {
    private Connection connection;

    public LikeService() {
        connection = DataSource.getInstance().getConnection();
    }

    public void ajouterLike(Like like) {
        String req = "INSERT INTO likes (id_Blog, id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, like.getId_Blog());
            ps.setInt(2, like.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du like : " + e.getMessage());
        }
    }

    public void supprimerLike(Like like) {
        String req = "DELETE FROM likes WHERE id_Blog = ? AND id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, like.getId_Blog());
            ps.setInt(2, like.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du like : " + e.getMessage());
        }
    }

    public int getNombreLikes(int id_Blog) {
        String req = "SELECT COUNT(*) FROM likes WHERE id_Blog = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id_Blog);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des likes : " + e.getMessage());
        }
        return 0;
    }

    public boolean hasUserLikedPost(int id_Blog, int id) {
        String req = "SELECT COUNT(*) FROM likes WHERE id_Blog = ? AND id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id_Blog);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du like : " + e.getMessage());
        }
        return false;
    }

    public List<Like> getLikesByPost(int id_Blog) {
        List<Like> likes = new ArrayList<>();
        String req = "SELECT l.*, u.Prenom, u.Nom FROM likes l JOIN utilisateur u ON l.id = u.id WHERE l.id_Blog = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id_Blog);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Like like = new Like();
                like.setId_like(rs.getInt("id_like"));
                like.setId_Blog(rs.getInt("id_Blog"));
                like.setId(rs.getInt("id"));
                like.setDate_like(rs.getTimestamp("date_like"));

                User utilisateur = new User();
                utilisateur.setPrenom(rs.getString("Prenom"));  // Corrigé : non-static
                utilisateur.setNom(rs.getString("Nom"));
                like.setUtilisateur(utilisateur);

                likes.add(like);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des likes : " + e.getMessage());
        }
        return likes;
    }
}
