package com.esprit.modules;

public class Equipement {
    private int equipementId;
    private String nomEquipement;
    private String type; //
    private int quantite;
    private String description;
    private boolean estDisponible; // Pour savoir si l'équipement est actuellement disponible

    // Constructeur
    public Equipement(int id, String nom, String type, int quantite, String description, boolean estDisponible) {
        this.equipementId= id;
        this.nomEquipement = nom;
        this.type = type;
        this.quantite = quantite;
        this.description = description;
        this.estDisponible = estDisponible;
    }

    public Equipement(String nom, String type, int quantite, String description, boolean estDisponible) {
        this.nomEquipement = nom;
        this.type = type;
        this.quantite = quantite;
        this.description = description;
        this.estDisponible = estDisponible;
    }

    // Getters et Setters
    public int getId() {
        return equipementId;
    }

    public void setId(int id) {
        this.equipementId = id;
    }

    public String getNom() {
        return nomEquipement;
    }

    public void setNom(String nom) {
        this.nomEquipement = nom;
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
        return "Equipement [equipementId=" + equipementId + ", nom=" + nomEquipement+ ", type=" + type + ", quantite=" + quantite + ", description="
                + description + ", estDisponible=" + estDisponible + "]";
    }
}
