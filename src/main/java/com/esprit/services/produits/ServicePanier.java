package com.esprit.services.produits;

import java.util.ArrayList;
import java.util.List;

public class ServicePanier {

    private List<String> panier;

    public ServicePanier() {
        panier = new ArrayList<>();
        panier.add("Produit 1");
        panier.add("Produit 2");
        panier.add("Produit 3");
    }

    public List<String> getItemsPanier() {
        return panier;
    }

    public void ajouterProduit(String produit) {
        panier.add(produit);
    }

    public void supprimerProduit(String produit) {
        panier.remove(produit);
    }

    public void viderPanier() {
        panier.clear();
    }

    public void validerPanier() {
        System.out.println("Validation de la commande avec les produits: " + panier);
        panier.clear();
    }
}
