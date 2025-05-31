package com.esprit.services;

import com.esprit.utils.DataSource;
import java.sql.*;

public class LikeService {
    private final Connection connection = DataSource.getInstance().getConnection();

    public void ajouterLike(int blogId, int userId) throws SQLException {
        String query = "INSERT INTO `like(` (id_Blog, id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void supprimerLike(int blogId, int userId) throws SQLException {
        String query = "DELETE FROM `like(` WHERE id_Blog = ? AND id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public boolean hasUserLiked(int blogId, int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM `like(` WHERE id_Blog = ? AND id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public int getLikeCount(int blogId) throws SQLException {
        String query = "SELECT COUNT(*) FROM `like(` WHERE id_Blog = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}