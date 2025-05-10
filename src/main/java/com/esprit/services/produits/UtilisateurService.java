package com.esprit.services.produits;

import com.esprit.modules.produits.Utilisateur;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilisateurService {
    private static final Logger logger = Logger.getLogger(UtilisateurService.class.getName());

    public Optional<Utilisateur> trouverParId(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DataSource.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapToUtilisateur(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la recherche de l'utilisateur par ID: " + id, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return Optional.empty();
    }

    private Utilisateur mapToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
            user.setDateInscription(rs.getTimestamp("date_inscription"));
        return user;
    }

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close(); // Note: Dans un pool de connexions, cela peut simplement retourner la connexion au pool
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erreur lors de la fermeture des ressources JDBC", e);
        }
    }
}