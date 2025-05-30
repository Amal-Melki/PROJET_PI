package com.esprit.models;

/**
 * 
 */
public class Espace {
    private int espaceId;
    private String nom;
    private String type;
    private int capacite;
    private String localisation;
    private double prix;
    private boolean disponibilite;
    private String photoUrl;
    private String description;
    private String image;

    public Espace(){};

    public Espace(int espaceId, String nom, String type, int capacite, String localisation, double prix, boolean disponibilite) {
        this.espaceId = espaceId;
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prix = prix;
        this.disponibilite = disponibilite;
    }
    
    public Espace(int espaceId, String nom, String type, int capacite, String localisation, double prix, boolean disponibilite, String photoUrl) {
        this.espaceId = espaceId;
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prix = prix;
        this.disponibilite = disponibilite;
        this.photoUrl = photoUrl;
    }

    public Espace(String nom, String type, int capacite, String localisation, double prix, boolean disponibilite) {
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prix = prix;
        this.disponibilite = disponibilite;
    }
    
    public Espace(String nom, String type, int capacite, String localisation, double prix, boolean disponibilite, String photoUrl) {
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prix = prix;
        this.disponibilite = disponibilite;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return espaceId; // Pour maintenir la compatibilité
    }

    public void setId(int id) {
        this.espaceId = id; // Pour maintenir la compatibilité
    }
    
    public int getEspaceId() {
        return espaceId;
    }
    
    public void setEspaceId(int espaceId) {
        this.espaceId = espaceId;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "Espace{" +
                "espaceId=" + espaceId +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", capacite=" + capacite +
                ", localisation='" + localisation + '\'' +
                ", prix=" + prix +
                ", disponibilite=" + disponibilite +
                ", photoUrl='" + photoUrl + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setImage(String imagePath) {
        this.image = imagePath;
        this.photoUrl = imagePath; // Pour maintenir la compatibilité
    }

    public String getImage() {
        return image != null ? image : photoUrl;
    }

    public boolean isDisponible() {
        return disponibilite;
    }

    /**
     * Retourne le prix de location de l'espace
     * @return le prix de location
     */
    public double getPrixLocation() {
        return prix;
    }
}
