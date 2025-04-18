package com.esprit.services.produits;


import com.esprit.modules.produits.CommandeProduit;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommandeProduit implements IService<CommandeProduit> {

    private Connection connection;

    public ServiceCommandeProduit() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(CommandeProduit commande) {
        String req = "INSERT INTO commande_produit(produit_id, utilisateur_id, date_commande, quantite, prix_total, statut) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commande.getProduitId());
            pst.setInt(2, commande.getUtilisateurId());
            pst.setDate(3, commande.getDateCommande());
            pst.setInt(4, commande.getQuantite());
            pst.setDouble(5, commande.getPrixTotal());
            pst.setString(6, commande.getStatut());

            pst.executeUpdate();
            System.out.println("Commande ajoutée avec succès !");

            // Mettre à jour le stock du produit
            ServiceProduitDerive spd = new ServiceProduitDerive();
            spd.mettreAJourStock(commande.getProduitId(), commande.getQuantite());

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }

    @Override
    public void modifier(CommandeProduit commande) {
        String req = "UPDATE commande_produit SET produit_id=?, utilisateur_id=?, date_commande=?, quantite=?, prix_total=?, statut=? WHERE id=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commande.getProduitId());
            pst.setInt(2, commande.getUtilisateurId());
            pst.setDate(3, commande.getDateCommande());
            pst.setInt(4, commande.getQuantite());
            pst.setDouble(5, commande.getPrixTotal());
            pst.setString(6, commande.getStatut());
            pst.setInt(7, commande.getId());

            pst.executeUpdate();
            System.out.println("Commande modifiée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de la commande : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(CommandeProduit commande) {
        String req = "DELETE FROM commande_produit WHERE id=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commande.getId());
            pst.executeUpdate();
            System.out.println("Commande supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la commande : " + e.getMessage());
        }
    }

    @Override
    public List<CommandeProduit> recuperer() {
        List<CommandeProduit> commandes = new ArrayList<>();
        String req = "SELECT * FROM commande_produit";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                commandes.add(new CommandeProduit(
                        rs.getInt("id"),
                        rs.getInt("produit_id"),
                        rs.getInt("utilisateur_id"),
                        rs.getDate("date_commande"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_total"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return commandes;
    }

    // Méthode pour récupérer les commandes d'un utilisateur spécifique
    public List<CommandeProduit> recupererParUtilisateur(int utilisateurId) {
        List<CommandeProduit> commandes = new ArrayList<>();
        String req = "SELECT * FROM commande_produit WHERE utilisateur_id = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, utilisateurId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                commandes.add(new CommandeProduit(
                        rs.getInt("id"),
                        rs.getInt("produit_id"),
                        rs.getInt("utilisateur_id"),
                        rs.getDate("date_commande"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_total"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes utilisateur : " + e.getMessage());
        }

        return commandes;
    }
}