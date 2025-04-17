package com.esprit.tests;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ProduitDeriveDAO;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class mainProg {
    public static void main(String[] args) {

        try (Connection connection = DataSource.getInstance().getConnection()) {
            ProduitDeriveDAO dao = new ProduitDeriveDAO(connection);

            ProduitDerive produit = new ProduitDerive(0,"Casquette", "Casquette officielle de l'événement", 15.0, 50, "Accessoires", new Date());
            dao.ajouterProduit(produit);
            System.out.println("Produit ajouté avec succès :");
            System.out.println(produit);

            List<ProduitDerive> produits = dao.getAllProduits();
            System.out.println("\nListe de tous les produits dérivés :");
            for (ProduitDerive p : produits) {
                System.out.println(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'accès à la base de données :");
            System.out.println(e.getMessage());
        }
    }
}
