package com.esprit.modules;
public class Espace {
    private int espaceId;
    private String nomEspace;
    private String type;  // Ex: Salle, Extérieur, etc.
    private double prix;  // Prix par jour ou par heure
    private int capacite; // Nombre maximal de personnes que l'espace peut accueillir
    private String localisation; // Lieu de l'espace (adresse)

    // Constructeur
    public Espace(int id, String nom, String type, double prix, int capacite, String localisation) {
        this.espaceId = id;
        this.nomEspace = nom;
        this.type = type;
        this.prix = prix;
        this.capacite = capacite;
        this.localisation = localisation;
    }
    public Espace(String nom, String type, double prix, int capacite, String localisation) {
        this.nomEspace= nom;
        this.type = type;
        this.prix = prix;
        this.capacite = capacite;
        this.localisation = localisation;
    }

    //constructor vide
    public Espace() {}


    // Getters et Setters
    public int getId() {
        return espaceId;
    }

    public void setId(int id) {
        this.espaceId = id;
    }

    public String getNom() {
        return nomEspace;
    }

    public void setNom(String nom) {
        this.nomEspace = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
}
