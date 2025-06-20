package com.esprit.services;

import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {

    private final Connection connection = DataSource.getInstance().getConnection();

    // Ajout d'un commentaire
    public void ajouterCommentaire(int blogId, int userId, String texte) throws SQLException {
        String query = "INSERT INTO commentaire (id, id_user, description, date_commentaire) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.setString(3, texte);
            ps.executeUpdate();
        }
    }

    // Récupérer la liste des commentaires avec le nom utilisateur
    public List<String> getCommentaires(int blogId) throws SQLException {
        String query = "SELECT u.nom_suser, c.description, c.date_commentaire " +
                "FROM commentaire c " +
                "JOIN user u ON c.id_user = u.id_user " +
                "WHERE c.id = ? " +
                "ORDER BY c.date_commentaire DESC";
        List<String> commentaires = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String user = rs.getString("nom_suser");
                String desc = rs.getString("description");
                Timestamp date = rs.getTimestamp("date_commentaire");
                commentaires.add("[" + date + "] " + user + ": " + desc);
            }
        }
        return commentaires;
    }
    public int getCommentCount(int blogId) throws SQLException {
        String query = "SELECT COUNT(*) FROM commentaire WHERE blog_id = ?";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, blogId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
