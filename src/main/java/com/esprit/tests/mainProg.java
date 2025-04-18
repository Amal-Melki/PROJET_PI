package com.esprit.tests;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.ServiceProduitDerive;
import com.esprit.modules.produits.CommandeProduit;
import com.esprit.services.produits.ServiceCommandeProduit;

import java.sql.Date;
import java.util.List;

public class mainProg {
    public static void main(String[] args) {
        ServiceProduitDerive spd = new ServiceProduitDerive();
        ServiceCommandeProduit scp = new ServiceCommandeProduit();

        try {

            System.out.println("=== TEST AJOUT PRODUIT ===");
            ProduitDerive produit = creerProduitTest();
            spd.ajouter(produit);
            System.out.println("Produit ajouté avec succès: " + produit.getNom());


            System.out.println("\n=== TEST COMMANDE ===");
            CommandeProduit commande = creerCommandeTest(produit.getId());
            scp.ajouter(commande);
            System.out.println("Commande ajoutée avec succès pour le produit ID: " + commande.getProduitId());


            afficherResultats(spd, scp);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ProduitDerive creerProduitTest() {
        return new ProduitDerive(
                "T-shirt Event",
                "Vêtements",
                25.99,
                100,
                "T-shirt coton avec logo de l'événement",
                "http://example.com/tshirt.jpg"
        );
    }

    private static CommandeProduit creerCommandeTest(int produitId) {
        return new CommandeProduit(
                produitId, // Utilisation de l'ID du produit créé
                1, // ID de l'utilisateur
                Date.valueOf("2024-04-15"),
                2, // Quantité
                51.98, // Prix total
                "EN_COURS"
        );
    }

    private static void afficherResultats(ServiceProduitDerive spd, ServiceCommandeProduit scp) {
        System.out.println("\n=== LISTE DES PRODUITS ===");
        List<ProduitDerive> produits = spd.recuperer();
        if (produits.isEmpty()) {
            System.out.println("Aucun produit trouvé.");
        } else {
            produits.forEach(p -> System.out.printf(
                    "ID: %d | Nom: %-15s | Catégorie: %-10s | Prix: %.2f | Stock: %d%n",
                    p.getId(), p.getNom(), p.getCategorie(), p.getPrix(), p.getStock()
            ));
        }

        System.out.println("\n=== LISTE DES COMMANDES ===");
        List<CommandeProduit> commandes = scp.recuperer();
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande trouvée.");
        } else {
            commandes.forEach(c -> System.out.printf(
                    "ID: %d | Produit ID: %d | User ID: %d | Date: %s | Qte: %d | Total: %.2f | Statut: %s%n",
                    c.getId(), c.getProduitId(), c.getUtilisateurId(),
                    c.getDateCommande(), c.getQuantite(), c.getPrixTotal(), c.getStatut()
            ));
        }
    }
}