package com.esprit.services.produits;

import com.esprit.modules.produits.*;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanierService {
    private static final Logger logger = Logger.getLogger(PanierService.class.getName());

    public Optional<Panier> getPanierActif(int utilisateurId) {
        String sql = "SELECT * FROM paniers WHERE utilisateur_id = ? AND est_actif = TRUE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DataSource.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, utilisateurId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapToPanier(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération du panier actif", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return Optional.empty();
    }

    public void creerPanier(Panier panier) {
        String sql = "INSERT INTO paniers (utilisateur_id) VALUES (?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DataSource.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, panier.getUtilisateurId());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                panier.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la création du panier", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public List<PanierItem> getItemsPanier(int panierId) {
        List<PanierItem> items = new ArrayList<>();
        String sql = "SELECT pi.*, pd.nom as nom_produit, pd.prix as prix_unitaire " +
                "FROM panier_items pi " +
                "JOIN produits_derives pd ON pi.produit_id = pd.id " +
                "WHERE pi.panier_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DataSource.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, panierId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapToPanierItem(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des items du panier", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return items;
    }

    // ... (autres méthodes avec le même pattern de gestion des ressources)

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erreur lors de la fermeture des ressources", e);
        }
    }

    private Panier mapToPanier(ResultSet rs) throws SQLException {
        Panier panier = new Panier();
        panier.setId(rs.getInt("id"));
        panier.setUtilisateurId(rs.getInt("utilisateur_id"));
        panier.setDateCreation(rs.getTimestamp("date_creation"));
        panier.setEstActif(rs.getBoolean("est_actif"));
        return panier;
    }

    private PanierItem mapToPanierItem(ResultSet rs) throws SQLException {
        PanierItem item = new PanierItem();
        item.setId(rs.getInt("id"));
        item.setPanierId(rs.getInt("panier_id"));
        item.setProduitId(rs.getInt("produit_id"));
        item.setNomProduit(rs.getString("nom_produit"));
        item.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        item.setQuantite(rs.getInt("quantite"));
        item.setDateAjout(rs.getTimestamp("date_ajout"));
        return item;
    }
}