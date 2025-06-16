package com.esprit.tests;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.Admin.ServiceProduitDerive;
import com.esprit.modules.produits.CommandeProduit;

import java.sql.Date;
import java.util.List;

public class mainProg {

    public static void main(String[] args) {
        ServiceProduitDerive serviceProduit = new ServiceProduitDerive();

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

        System.out.println("\nListe des produits dérivés :");
        List<ProduitDerive> produits = serviceProduit.recuperer();
        for (ProduitDerive p : produits) {
            System.out.println(p);
        }
    }
}
