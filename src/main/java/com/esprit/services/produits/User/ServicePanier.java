package com.esprit.services.produits.User;

import com.esprit.modules.produits.ProduitDerive;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator; // Ajouté pour removeProduitFromPanier
import java.util.List;

public class ServicePanier {
    // Ceci est une simulation de panier en mémoire.
    // Dans une vraie application, cela pourrait être persistant (DB, session, etc.)
    private static final List<ProduitDerive> panier = new ArrayList<>();

    public boolean ajouterProduitAuPanier(ProduitDerive produit) {
        if (produit != null) {
            panier.add(produit);
            System.out.println("Produit ajouté au panier: " + produit.getNom());
            return true;
        }
        return false;
    }

    public List<ProduitDerive> getProduitsDansPanier() {
        return new ArrayList<>(panier); // Retourne une COPIE pour éviter les ConcurrentModificationException
    }

    public void clearPanier() {
        panier.clear();
        System.out.println("Panier vidé.");
    }

    public double calculerTotalPanier() {
        return panier.stream()
                .mapToDouble(ProduitDerive::getPrix)
                .sum();
    }

    public boolean removeProduitFromPanier(ProduitDerive produitToRemove) {
        Iterator<ProduitDerive> iterator = panier.iterator();
        while (iterator.hasNext()) {
            ProduitDerive produit = iterator.next();
            // Comparaison basée sur l'ID du produit pour s'assurer de retirer le bon article
            if (produit.getId() == produitToRemove.getId()) {
                iterator.remove();
                System.out.println("Produit retiré du panier: " + produitToRemove.getNom());
                return true;
            }
        }
        return false;
    }
}