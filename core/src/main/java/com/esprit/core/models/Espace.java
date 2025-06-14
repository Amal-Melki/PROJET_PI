package com.esprit.core.models;

public class Espace {
    // Attributs
    private int id;
    private String nom;
    private String type;
    private int capacite;
    private boolean disponibilite;

    // Constructeurs
    public Espace() {}

    public Espace(String nom, String type, int capacite) {
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.disponibilite = true;
    }

    // Getters & Setters
    // ... (à compléter)
}
