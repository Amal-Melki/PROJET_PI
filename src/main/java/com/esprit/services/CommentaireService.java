package com.esprit.services;

import com.esprit.utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {
    private final Connection connection = DataSource.getInstance().getConnection();

    public void ajouterCommentaire(int blogId, int userId, String texte) throws SQLException {
        String query = "INSERT INTO `commentaire` (id_Blog, id, description, date_Commentaire) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.setString(3, texte);
            ps.executeUpdate();
        }
    }

    public List<String> getCommentaires(int blogId) throws SQLException {
        String query = "SELECT u.username, c.description FROM `commentaire` c " +
                "JOIN pos u ON c.id = u.id " +
                "WHERE c.id_Blog = ? " +
                "ORDER BY c.date_Commentaire DESC";
        List<String> commentaires = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                commentaires.add(rs.getString("username") + ": " + rs.getString("description"));
            }
        }
        return commentaires;
    }
}