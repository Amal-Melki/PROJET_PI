package com.esprit.services.produits;


import com.esprit.modules.produits.ProduitDerive;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProduitDerive implements IService<ProduitDerive> {

    private Connection connection;

    public ServiceProduitDerive() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(ProduitDerive produit) {
        String req = "INSERT INTO produit_derive(nom, categorie, prix, stock, description, image_url) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, produit.getNom());
            pst.setString(2, produit.getCategorie());
            pst.setDouble(3, produit.getPrix());
            pst.setInt(4, produit.getStock());
            pst.setString(5, produit.getDescription());
            pst.setString(6, produit.getImageUrl());

            pst.executeUpdate();
            System.out.println("Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
    }

    @Override
    public void modifier(ProduitDerive produit) {
        String req = "UPDATE produit_derive SET nom=?, categorie=?, prix=?, stock=?, description=?, image_url=? WHERE id=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, produit.getNom());
            pst.setString(2, produit.getCategorie());
            pst.setDouble(3, produit.getPrix());
            pst.setInt(4, produit.getStock());
            pst.setString(5, produit.getDescription());
            pst.setString(6, produit.getImageUrl());
            pst.setInt(7, produit.getId());

            pst.executeUpdate();
            System.out.println("Produit modifié avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du produit : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(ProduitDerive produit) {
        String req = "DELETE FROM produit_derive WHERE id=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produit.getId());
            pst.executeUpdate();
            System.out.println("Produit supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit : " + e.getMessage());
        }
    }

    @Override
    public List<ProduitDerive> recuperer() {
        List<ProduitDerive> produits = new ArrayList<>();
        String req = "SELECT * FROM produit_derive";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                produits.add(new ProduitDerive(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("categorie"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getString("image_url")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }

        return produits;
    }


    public void mettreAJourStock(int produitId, int quantite) {
        String req = "UPDATE produit_derive SET stock = stock - ? WHERE id = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, quantite);
            pst.setInt(2, produitId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
        }
    }



    public boolean supprimerProduit(int id) {
        String req = "DELETE FROM produit_derive WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // true si au moins une ligne a été supprimée
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit (par ID) : " + e.getMessage());
            return false;
        }
    }

    public List<ProduitDerive> obtenirTousLesProduits() {
        return recuperer(); // Appelle simplement la méthode existante
    }

}

