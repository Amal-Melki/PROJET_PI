package com.esprit.services.produits.User; // Ou com.esprit.services.produits.User si c'est ta structure

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.utils.DataSource; // Utilisation de TON DataSource

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceProduitDetails {
    private Connection connection;

    public ServiceProduitDetails() {
        connection = DataSource.getInstance().getConnection();
        if (connection == null) {
            System.err.println("DEBUG: ServiceProduitDetails: La connexion à la base de données est NULL. Vérifiez DataSource.java et votre serveur MySQL.");
        }
    }

    public ProduitDerive getProduitById(int id) {
        ProduitDerive produit = null;
        String query = "SELECT id, nom, categorie, description, prix, image_url FROM produit_derive WHERE id = ?";
        System.out.println("DEBUG: ServiceProduitDetails: Tentative de récupération du produit avec ID: " + id);

        if (connection == null) {
            System.err.println("ERREUR: ServiceProduitDetails: Impossible d'exécuter la requête. La connexion à la DB est null.");
            return null;
        }

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            System.out.println("DEBUG: ServiceProduitDetails: Exécution de la requête SQL: " + query + " avec ID=" + id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    produit = new ProduitDerive(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("categorie"),
                            rs.getString("description"), // <-- Problème ici : c'est un String, pas un double
                            rs.getDouble("prix"),       // <-- Problème ici : c'est un double, pas un String
                            rs.getString("image_url")
                    );
                    System.out.println("DEBUG: ServiceProduitDetails: Produit trouvé dans la DB: " + produit.getNom() + " (ID: " + produit.getId() + ")");
                    System.out.println("DEBUG: Image URL du produit depuis DB: " + produit.getImageUrl());
                } else {
                    System.out.println("DEBUG: ServiceProduitDetails: Aucun produit trouvé avec l'ID: " + id + " dans la base de données. (Cela pourrait être la cause des champs 'null')");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERREUR: ServiceProduitDetails: Erreur SQL lors de la récupération du produit par ID " + id + " : " + e.getMessage());
            e.printStackTrace();
        }
        return produit;
    }

    // Méthodes simulées pour l'achat et la suppression du panier
    public boolean acheterProduit(ProduitDerive produit) {
        System.out.println("SERVICE : Logique d'achat déclenchée pour le produit : " + produit.getNom() + " (ID: " + produit.getId() + ")");
        return true;
    }

    public boolean supprimerDuPanier(ProduitDerive produit) {
        System.out.println("SERVICE : Logique de suppression du panier déclenchée pour le produit : " + produit.getNom() + " (ID: " + produit.getId() + ")");
        return true;
    }
}