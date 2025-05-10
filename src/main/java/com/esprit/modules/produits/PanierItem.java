package com.esprit.modules.produits;

import java.sql.Timestamp;

public class PanierItem {
    private int id;
    private int panierId;
    private int produitId;
    private String nomProduit;
    private double prixUnitaire;
    private int quantite;
    private Timestamp dateAjout;

    // Constructeurs
    public PanierItem() {}

    public PanierItem(int id, int panierId, int produitId, String nomProduit,
                      double prixUnitaire, int quantite, Timestamp dateAjout) {
        this.id = id;
        this.panierId = panierId;
        this.produitId = produitId;
        this.nomProduit = nomProduit;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
        this.dateAjout = dateAjout;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID doit être positif");
        }
        this.id = id;
    }

    public int getPanierId() {
        return panierId;
    }

    public void setPanierId(int panierId) {
        if (panierId <= 0) {
            throw new IllegalArgumentException("L'ID panier doit être positif");
        }
        this.panierId = panierId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        if (produitId <= 0) {
            throw new IllegalArgumentException("L'ID produit doit être positif");
        }
        this.produitId = produitId;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        if (nomProduit == null || nomProduit.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        this.nomProduit = nomProduit;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        if (prixUnitaire <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }
        this.prixUnitaire = prixUnitaire;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        this.quantite = quantite;
    }

    public Timestamp getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Timestamp dateAjout) {
        if (dateAjout == null) {
            throw new IllegalArgumentException("La date d'ajout ne peut pas être null");
        }
        this.dateAjout = dateAjout;
    }
}