package com.esprit.modules.produits;

import java.util.Date;

public class ProduitDerive {
    private int id;
    private String nom;
    private String description;
    private double prix;
    private int quantite;
    private String categorie;
    private Date dateAjout;

    // ✅ Constructeurs
    public ProduitDerive() {
    }

    public ProduitDerive(int id, String nom, String description, double prix, int quantite, String categorie, Date dateAjout) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantite = quantite;
        this.categorie = categorie;
        this.dateAjout = dateAjout;
    }

    public ProduitDerive(String nom, String description, double prix, int quantite, String categorie, Date dateAjout) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantite = quantite;
        this.categorie = categorie;
        this.dateAjout = dateAjout;
    }

    // ✅ Getters et Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    // ✅ toString
    @Override
    public String toString() {
        return "ProduitDerive{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", categorie='" + categorie + '\'' +
                ", dateAjout=" + dateAjout +
                '}';
    }
}
