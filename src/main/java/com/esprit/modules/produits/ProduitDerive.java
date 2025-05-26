package com.esprit.modules.produits;

import javafx.beans.property.*;

public class ProduitDerive {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty categorie = new SimpleStringProperty();
    private final DoubleProperty prix = new SimpleDoubleProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty imageUrl = new SimpleStringProperty();

    // Constructeur par défaut (utile pour les TableViews ou initialisation vide)
    public ProduitDerive() {
    }

    /**
     * Constructeur complet pour un ProduitDerive, incluant toutes les propriétés.
     * Utilisé généralement pour la création ou la lecture complète d'un objet.
     */
    public ProduitDerive(int id, String nom, String categorie, double prix, int stock, String description, String imageUrl) {
        this.id.set(id);
        this.nom.set(nom);
        this.categorie.set(categorie);
        this.prix.set(prix);
        this.stock.set(stock);
        this.description.set(description);
        this.imageUrl.set(imageUrl);
    }

    /**
     * Constructeur pour l'ajout d'un nouveau produit (sans ID, car l'ID est auto-généré par la base de données).
     */
    public ProduitDerive(String nom, String categorie, double prix, int stock, String description, String imageUrl) {
        this.nom.set(nom);
        this.categorie.set(categorie);
        this.prix.set(prix);
        this.stock.set(stock);
        this.description.set(description);
        this.imageUrl.set(imageUrl);
    }

    /**
     * Constructeur spécifique pour la récupération des détails d'un produit depuis la DB,
     * correspondant aux colonnes sélectionnées dans ServiceProduitDetails (sans le stock).
     * C'est ce constructeur qui était vide et causait le problème.
     */
    public ProduitDerive(int id, String nom, String categorie, String description, double prix, String imageUrl) {
        this.id.set(id);
        this.nom.set(nom);
        this.categorie.set(categorie);
        this.description.set(description);
        this.prix.set(prix);
        this.imageUrl.set(imageUrl);
        // Le stock est laissé à sa valeur par défaut (0) car non fourni dans ce constructeur
    }

    // --- Les getters classiques (pour accéder aux valeurs) ---
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getCategorie() {
        return categorie.get();
    }

    public void setCategorie(String categorie) {
        this.categorie.set(categorie);
    }

    public double getPrix() {
        return prix.get();
    }

    public void setPrix(double prix) {
        this.prix.set(prix);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    // --- Les Property getters (pour le binding JavaFX) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty categorieProperty() {
        return categorie;
    }

    public DoubleProperty prixProperty() {
        return prix;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    // Méthode toString pour un affichage utile de l'objet
    @Override
    public String toString() {
        return "ProduitDerive{" +
                "id=" + id.get() +
                ", nom='" + nom.get() + '\'' +
                ", categorie='" + categorie.get() + '\'' +
                ", prix=" + prix.get() +
                ", stock=" + stock.get() +
                ", description='" + description.get() + '\'' +
                ", imageUrl='" + imageUrl.get() + '\'' +
                '}';
    }
}