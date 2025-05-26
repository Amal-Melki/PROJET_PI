package com.esprit.services.produits.User;

import com.esprit.modules.produits.ProduitDerive;
import java.util.ArrayList;
import java.util.Collections;
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
        return Collections.unmodifiableList(panier); // Retourne une liste non modifiable
    }

    public void clearPanier() {
        panier.clear();
        System.out.println("Panier vidé.");
    }

    // Tu pourrais ajouter d'autres méthodes comme supprimer un produit spécifique, etc.
}