package com.esprit.services.produits.User;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceProduitDeriveUser {

    private Connection connection;

    public ServiceProduitDeriveUser() {
        connection = DataSource.getInstance().getConnection();
    }

    public List<ProduitDerive> obtenirTousLesProduits() throws SQLException {
        List<ProduitDerive> produits = new ArrayList<>();
        String req = "SELECT id, nom, categorie, prix, stock, description, image_url FROM produit_derive";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                ProduitDerive produit = new ProduitDerive();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setCategorie(rs.getString("categorie"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setStock(rs.getInt("stock"));
                produit.setDescription(rs.getString("description"));
                produit.setImageUrl(rs.getString("image_url"));
                produits.add(produit);
            }
        }
        return produits;
    }

    // Méthode existante pour la recherche par nom
    public List<ProduitDerive> rechercherProduitsParNom(String nomRecherche) throws SQLException {
        List<ProduitDerive> produitsTrouves = new ArrayList<>();
        String req = "SELECT id, nom, categorie, prix, stock, description, image_url FROM produit_derive WHERE UPPER(nom) LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, "%" + nomRecherche.toUpperCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProduitDerive produit = new ProduitDerive();
                    produit.setId(rs.getInt("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setStock(rs.getInt("stock"));
                    produit.setDescription(rs.getString("description"));
                    produit.setImageUrl(rs.getString("image_url"));
                    produitsTrouves.add(produit);
                }
            }
        }
        return produitsTrouves;
    }

    // Nouvelle méthode pour filtrer les produits par catégorie
    public List<ProduitDerive> filtrerProduitsParCategorie(String categorieRecherche) throws SQLException {
        List<ProduitDerive> produitsTrouves = new ArrayList<>();
        String req = "SELECT id, nom, categorie, prix, stock, description, image_url FROM produit_derive WHERE UPPER(categorie) LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, "%" + categorieRecherche.toUpperCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProduitDerive produit = new ProduitDerive();
                    produit.setId(rs.getInt("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setStock(rs.getInt("stock"));
                    produit.setDescription(rs.getString("description"));
                    produit.setImageUrl(rs.getString("image_url"));
                    produitsTrouves.add(produit);
                }
            }
        }
        return produitsTrouves;
    }
}