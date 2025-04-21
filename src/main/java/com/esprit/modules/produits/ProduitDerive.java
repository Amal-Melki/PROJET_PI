package com.esprit.modules.produits;

import java.util.Date;

public class ProduitDerive {
    private int id;
    private String nom;
    private String categorie;
    private double prix;
    private int stock;
    private String description;
    private String imageUrl;


    public ProduitDerive() {
    }

    public ProduitDerive(int id, String nom, String categorie, double prix, int stock, String description, String imageUrl) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.stock = stock;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public ProduitDerive(String nom, String categorie, double prix, int stock, String description, String imageUrl) {
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.stock = stock;
        this.description = description;
        this.imageUrl = imageUrl;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ProduitDerive{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prix=" + prix +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
