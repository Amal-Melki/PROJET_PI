package com.esprit.modules;

import java.sql.Timestamp;
import java.util.Objects;

public class Blog {
    private int id;
    private String titre;
    private String contenu;
    private String imagePath;
    private Timestamp date;
    private CategorieEnum categorie;
    private double latitude;
    private double longitude;


    public Blog(String titre, String contenu, String imagePath, Timestamp date, CategorieEnum categorie) {
        this.titre = titre;
        this.contenu = contenu;
        this.imagePath = imagePath;
        this.date = date;
        this.categorie = categorie;
    }

    public Blog(int id, String contenu, String titre, String imagePath, Timestamp date, CategorieEnum categorie) {
        this.id = id;
        this.contenu = contenu;
        this.titre = titre;
        this.imagePath = imagePath;
        this.date = date;
        this.categorie = categorie;
    }

    public Blog() {
        // constructeur vide
    }
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id= id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getImage() {
        return imagePath;
    }

    public void setImage(String image) {
        this.imagePath = image;
    }

    public CategorieEnum getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieEnum categorie) {
        this.categorie = categorie;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", image='" + imagePath + '\'' +
                ", date=" + date +
                ", categorie='" + categorie + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Blog other)) return false;
        return id == other.id;
    }


}
