package com.esprit.services;

import com.esprit.modules.Share;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShareService {
    private final Connection connection;

    public ShareService() {
        connection = DataSource.getInstance().getConnection();
    }

    // Ajouter un partage
    public void ajouterShare(Share share) throws SQLException {
        String query = "INSERT INTO share (id, id_client, share_date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, share.getId());
            statement.setInt(2, share.getId_client());
            statement.setTimestamp(3, share.getShareDate());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    share.setId_share(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Supprimer un partage
    public void supprimerShare(int id_share) throws SQLException {
        String query = "DELETE FROM share WHERE id_share = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_share);
            statement.executeUpdate();
        }
    }

    // Nombre de partages pour un blog
    public int getShareCount(int id_blog) throws SQLException {
        String query = "SELECT COUNT(*) FROM share WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_blog);
            ResultSet rs = statement.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Vérifier si un client a partagé un blog
    public boolean hasClientShared(int id_blog, int id_client) throws SQLException {
        String query = "SELECT COUNT(*) FROM share WHERE id = ? AND id_client = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_blog);
            statement.setInt(2, id_client);
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Récupérer tous les partages d'un client
    public List<Share> getSharesByClient(int id_client) throws SQLException {
        List<Share> shares = new ArrayList<>();
        String query = "SELECT * FROM share WHERE id_client = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_client);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                shares.add(new Share(
                        rs.getInt("id"),
                        rs.getInt("id_client"),
                        rs.getTimestamp("share_date")
                ));
            }
        }
        return shares;
    }

    // Récupérer les IDs des blogs partagés par un client
    public List<Integer> getSharedBlogIds(int id_client) throws SQLException {
        List<Integer> blogIds = new ArrayList<>();
        String query = "SELECT id FROM share WHERE id_client = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_client);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                blogIds.add(rs.getInt("id"));
            }
        }
        return blogIds;
    }
}