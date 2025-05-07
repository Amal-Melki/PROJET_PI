package com.esprit.modules;
import java.time.LocalDate;



public class Espace {
    private int espaceId;
    private String nomEspace;
    private String type;
    private int capacite;
    private String localisation;
    private double prix;
    private boolean disponibilite;

    public Espace(int id, String nom, String type, int capacite, String localisation, double prix, boolean disponibilite) {
        this.espaceId = id;
        this.nomEspace = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prix = prix;
        this.disponibilite = disponibilite;
    }

    public Espace(String nom, String type, int capacite, String localisation, double prix, boolean disponibilite) {
        this.nomEspace = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prix = prix;
        this.disponibilite = disponibilite;
    }

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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    @Override
    public String toString() {
        return "Espace{" +
                "id=" + espaceId +
                ", nom='" + nomEspace + '\'' +
                ", type='" + type + '\'' +
                ", capacite=" + capacite +
                ", localisation='" + localisation + '\'' +
                ", prix=" + prix +
                ", disponibilite=" + disponibilite +
                '}';
    }
}


