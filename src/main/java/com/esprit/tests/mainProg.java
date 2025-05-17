package com.esprit.tests;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import com.esprit.modules.produits.CommandeProduit;
import com.esprit.services.produits.ServiceCommandeProduit;

import java.sql.Date;
import java.util.List;

public class mainProg {
    public static void main(String[] args) {
        ServiceProduitDerive serviceProduit = new ServiceProduitDerive();
        ServiceCommandeProduit serviceCommande = new ServiceCommandeProduit();

        // ✅ AJOUT PRODUIT DÉRIVÉ
        ProduitDerive produit = new ProduitDerive(
                "T-shirt Event",
                "Vêtements",
                25.99,
                100,
                "T-shirt coton avec logo de l'événement",
                "http://example.com/tshirt.jpg"
        );
        serviceProduit.ajouter(produit);
        System.out.println("Produit ajouté : " + produit.getNom());

        // ✅ AFFICHAGE DES PRODUITS
        System.out.println("\nListe des produits dérivés :");
        List<ProduitDerive> produits = serviceProduit.recuperer();
        for (ProduitDerive p : produits) {
            System.out.println(p);
        }

        // ✅ AJOUT COMMANDE
        CommandeProduit commande = new CommandeProduit(
                produit.getId(),       // produitId
                1,                     // utilisateurId
                Date.valueOf("2024-04-15"),
                2,                     // Quantité
                51.98,                 // Prix total
                "EN_COURS"
        );
        serviceCommande.ajouter(commande);
        System.out.println("\nCommande ajoutée pour le produit ID : " + commande.getProduitId());

        // ✅ AFFICHAGE DES COMMANDES
        System.out.println("\nListe des commandes :");
        List<CommandeProduit> commandes = serviceCommande.recuperer();
        for (CommandeProduit cmd : commandes) {
            System.out.println(cmd);
        }
    }
}
