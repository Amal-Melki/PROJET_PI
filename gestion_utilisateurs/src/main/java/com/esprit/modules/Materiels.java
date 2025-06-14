package com.esprit.modules;
import jakarta.persistence.*;

@Entity
@Table(name = "materiel")
public class Materiels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String type;
    private int quantite;
    private String etat;
    private String description;
    private String image;
    private double prix; // ✅ Nouveau champ

    // Constructeurs
    public Materiels() {
    }

    public Materiels(int id, String nom, String type, int quantite, String etat, String description, String image, double prix) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.etat = etat;
        this.description = description;
        this.image = image;
        this.prix = prix;
    }

    public Materiels(String nom, String type, int quantite, String etat, String description, String image, double prix) {
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.etat = etat;
        this.description = description;
        this.image = image;
        this.prix = prix;
    }

    // Getters et Setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Pour afficher le nom dans les ComboBox ou ListView
    @Override
    public String toString() {
        return nom;
    }
}
