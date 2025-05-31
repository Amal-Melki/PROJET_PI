package com.esprit.services.produits;

import com.esprit.modules.produits.ProduitDerive;

import java.util.List;

public interface IService<T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(T t);
    List<T> recuperer();
    boolean modifierProd(ProduitDerive produit);
}
