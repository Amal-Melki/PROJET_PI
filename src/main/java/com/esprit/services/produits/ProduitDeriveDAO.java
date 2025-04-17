package com.esprit.services.produits;

import com.esprit.modules.produits.ProduitDerive;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProduitDeriveDAO implements IProduitDeriveDAO {
    private Connection connection;

    public ProduitDeriveDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void ajouterProduit(ProduitDerive produit) {
        String sql = "INSERT INTO produit_derive (nom, description, prix, quantite, categorie, date_ajout) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantite());
            stmt.setString(5, produit.getCategorie());
            stmt.setDate(6, new java.sql.Date(produit.getDateAjout().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProduitDerive getProduitById(int id) {
        String sql = "SELECT * FROM produit_derive WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduit(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ProduitDerive> getAllProduits() {
        List<ProduitDerive> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit_derive";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    @Override
    public void updateProduit(ProduitDerive produit) {
        String sql = "UPDATE produit_derive SET nom = ?, description = ?, prix = ?, quantite = ?, categorie = ?, date_ajout = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantite());
            stmt.setString(5, produit.getCategorie());
            stmt.setDate(6, new java.sql.Date(produit.getDateAjout().getTime()));
            stmt.setInt(7, produit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduit(int id) {
        String sql = "DELETE FROM produit_derive WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ProduitDerive mapResultSetToProduit(ResultSet rs) throws SQLException {
        return new ProduitDerive(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getDouble("prix"),
                rs.getInt("quantite"),
                rs.getString("categorie"),
                rs.getDate("date_ajout")
        );
    }
}
