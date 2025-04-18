package com.esprit.modules;

public class Materiels {
    private int id;
    private String nom;
    private String type;
    private int quantite;
    private String etat;
    private String description;

    // Constructeurs
    public Materiels() {
    }

    public Materiels(int id, String nom, String type, int quantite, String etat, String description) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.etat = etat;
        this.description = description;
    }

    public Materiels(String nom, String type, int quantite, String etat, String description) {
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.etat = etat;
        this.description = description;
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

    // Méthode toString()
   /* @Override
    public String toString() {
        return "Materiel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", quantite=" + quantite +
                ", etat='" + etat + '\'' +
                ", description='" + description + '\'' +
                '}';
    }*/

    @Override
    public String toString() {
        return nom; // Affiche uniquement le nom dans la ComboBox
    }

}

