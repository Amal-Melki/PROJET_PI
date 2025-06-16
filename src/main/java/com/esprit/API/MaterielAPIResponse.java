package com.esprit.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterielAPIResponse {

    private int id;
    private String nom;
    private int quantite;

    // Constructeurs
    public MaterielAPIResponse() {
    }

    public MaterielAPIResponse(int id, String nom, int quantite) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
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

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
