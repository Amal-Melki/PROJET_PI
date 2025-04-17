package com.esprit.services.produits;

import com.esprit.modules.produits.ProduitDerive;
import java.util.List;

public interface IProduitDeriveDAO {
    void ajouterProduit(ProduitDerive produit);
    ProduitDerive getProduitById(int id);
    List<ProduitDerive> getAllProduits();
    void updateProduit(ProduitDerive produit);
    void deleteProduit(int id);
}
