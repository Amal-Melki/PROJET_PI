package com.esprit.services;

import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeService {
    private final Connection connection;

    public LikeService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * Compte le nombre de likes pour un blog
     * @param blogId L'identifiant du blog
     * @param isClient true si on compte les likes des clients, false pour les users normaux
     * @return Le nombre de likes
     */
    public int getLikeCount(int blogId, boolean isClient) throws SQLException {
        String query = isClient ?
                "SELECT COUNT(*) FROM likes WHERE id_blog = ? AND id_client IS NOT NULL" :
                "SELECT COUNT(*) FROM likes WHERE id_blog = ? AND id_user IS NOT NULL";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Vérifie si un utilisateur a déjà liké un blog
     * @param blogId L'identifiant du blog
     * @param userId L'identifiant de l'utilisateur
     * @param isClient true si c'est un client, false pour un user normal
     * @return true si l'utilisateur a déjà liké, false sinon
     */
    public boolean hasUserLiked(int blogId, int userId, boolean isClient) throws SQLException {
        String query = isClient ?
                "SELECT COUNT(*) FROM likes WHERE id_blog = ? AND id_client = ?" :
                "SELECT COUNT(*) FROM likes WHERE id_blog = ? AND id_user = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Ajoute un like à un blog
     * @param blogId L'identifiant du blog
     * @param userId L'identifiant de l'utilisateur
     * @param isClient true si c'est un client, false pour un user normal
     */
    public void ajouterLike(int blogId, int userId, boolean isClient) throws SQLException {
        String query = isClient ?
                "INSERT INTO likes (id_blog, id_client) VALUES (?, ?)" :
                "INSERT INTO likes (id_blog, id_user) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Supprime un like d'un blog
     * @param blogId L'identifiant du blog
     * @param userId L'identifiant de l'utilisateur
     * @param isClient true si c'est un client, false pour un user normal
     */
    public void supprimerLike(int blogId, int userId, boolean isClient) throws SQLException {
        String query = isClient ?
                "DELETE FROM likes WHERE id_blog = ? AND id_client = ?" :
                "DELETE FROM likes WHERE id_blog = ? AND id_user = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

    // Version simplifiée pour rétrocompatibilité
    public boolean hasUserLiked(int blogId, int userId) throws SQLException {
        return hasUserLiked(blogId, userId, false);
    }

    public int getLikeCount(int blogId) throws SQLException {
        return getLikeCount(blogId, false);
    }
}