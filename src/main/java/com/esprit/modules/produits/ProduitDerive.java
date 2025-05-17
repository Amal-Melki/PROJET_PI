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

    public ProduitDerive() {
    }

    public ProduitDerive(int id, String nom, String categorie, double prix, int stock, String description, String imageUrl) {
        this.id.set(id);
        this.nom.set(nom);
        this.categorie.set(categorie);
        this.prix.set(prix);
        this.stock.set(stock);
        this.description.set(description);
        this.imageUrl.set(imageUrl);
    }

    public ProduitDerive(String nom, String categorie, double prix, int stock, String description, String imageUrl) {
        this.nom.set(nom);
        this.categorie.set(categorie);
        this.prix.set(prix);
        this.stock.set(stock);
        this.description.set(description);
        this.imageUrl.set(imageUrl);
    }

    public ProduitDerive(int i, String nom, String type, double prix, int quantite, int seuilAlerte, String description, String cheminImage, Object o) {
    }

    // Getters classiques
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

    // Properties pour le binding avec TableView
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
