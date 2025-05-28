package com.esprit.services.produits.User;

import com.esprit.modules.produits.ProduitDerive;
import java.util.LinkedHashMap; // Use LinkedHashMap to maintain insertion order
import java.util.Map;
import java.util.List;
import java.util.ArrayList; // For getProduitsDansPanier()

public class ServicePanier {
    // We now use a Map to store ProduitDerive -> Quantity
    // LinkedHashMap maintains the order of insertion, which is often desirable for a cart.
    private static final Map<ProduitDerive, Integer> panier = new LinkedHashMap<>();

    public boolean ajouterProduitAuPanier(ProduitDerive produit) {
        if (produit != null) {
            // If product already exists, increment quantity
            panier.put(produit, panier.getOrDefault(produit, 0) + 1);
            System.out.println("Produit ajouté au panier ou quantité augmentée: " + produit.getNom() + " (Quantité: " + panier.get(produit) + ")");
            return true;
        }
        return false;
    }

    // New method to adjust quantity
    public void setProduitQuantity(ProduitDerive produit, int quantity) {
        if (produit != null) {
            if (quantity <= 0) {
                panier.remove(produit); // Remove if quantity is 0 or less
                System.out.println("Produit retiré du panier: " + produit.getNom());
            } else {
                panier.put(produit, quantity); // Set new quantity
                System.out.println("Quantité de " + produit.getNom() + " mise à jour à: " + quantity);
            }
        }
    }

    // Retrieves the current quantity of a product in the cart
    public int getProduitQuantity(ProduitDerive produit) {
        return panier.getOrDefault(produit, 0);
    }

    // This method now returns a List of all distinct products in the cart
    public List<ProduitDerive> getProduitsDansPanier() {
        return new ArrayList<>(panier.keySet()); // Returns a list of unique products
    }

    // New method to get all cart items with quantities (useful for display and calculations)
    public Map<ProduitDerive, Integer> getPanierItemsWithQuantities() {
        return new LinkedHashMap<>(panier); // Return a copy
    }

    public void clearPanier() {
        panier.clear();
        System.out.println("Panier vidé.");
    }

    public double calculerTotalPanier() {
        return panier.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue()) // Price * Quantity
                .sum();
    }

    // Original remove method, now adjusted to handle the Map structure
    public boolean removeProduitFromPanier(ProduitDerive produitToRemove) {
        if (panier.containsKey(produitToRemove)) {
            panier.remove(produitToRemove);
            System.out.println("Produit retiré du panier: " + produitToRemove.getNom());
            return true;
        }
        return false;
    }
}