package com.esprit.modules.produits;

import java.sql.Timestamp;

public class Panier {
    private int id;
    private int utilisateurId;
    private Timestamp dateCreation;
    private boolean estActif;

    // Constructeurs
    public Panier() {}

    public Panier(int id, int utilisateurId, Timestamp dateCreation, boolean estActif) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.dateCreation = dateCreation;
        this.estActif = estActif;
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

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        if (utilisateurId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        this.utilisateurId = utilisateurId;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        if (dateCreation == null) {
            throw new IllegalArgumentException("La date de création ne peut pas être null");
        }
        this.dateCreation = dateCreation;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }
}