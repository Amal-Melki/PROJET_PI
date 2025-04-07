package com.esprit.modules;

public class Equipement {
    private int id;
    private String nom;
    private String type; //
    private int quantite;
    private String description;
    private boolean estDisponible; // Pour savoir si l'équipement est actuellement disponible

    // Constructeur
    public Equipement(int id, String nom, String type, int quantite, String description, boolean estDisponible) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.description = description;
        this.estDisponible = estDisponible;
    }

    public Equipement(String nom, String type, int quantite, String description, boolean estDisponible) {
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.description = description;
        this.estDisponible = estDisponible;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEstDisponible() {
        return estDisponible;
    }

    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }

    @Override
    public String toString() {
        return "Equipement [ideq=" + id + ", nom=" + nom + ", type=" + type + ", quantite=" + quantite + ", description="
                + description + ", estDisponible=" + estDisponible + "]";
    }
}
