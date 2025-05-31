package com.esprit.modules;

import java.sql.Timestamp;
import java.util.Objects;

public class Like {
    private int id_like;
    private int id_Blog;
    private int id;
    private Timestamp date_like;
    private User utilisateur;

    // Constructeur vide (nécessaire pour certaines opérations JDBC ou frameworks)
    public Like() {
    }

    // Constructeur sans utilisateur
    public Like(int id_like, int id_Blog, int id, Timestamp date_like) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id = id;
        this.date_like = date_like;
    }

    // Constructeur complet
    public Like(int id_like, int id_Blog, int id, Timestamp date_like, User utilisateur) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id = id;
        this.date_like = date_like;
        this.utilisateur = utilisateur;
    }

    // Getters et Setters
    public int getId_like() {
        return id_like;
    }

    public void setId_like(int id_like) {
        this.id_like = id_like;
    }

    public int getId_Blog() {
        return id_Blog;
    }

    public void setId_Blog(int id_Blog) {
        this.id_Blog = id_Blog;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDate_like() {
        return date_like;
    }

    public void setDate_like(Timestamp date_like) {
        this.date_like = date_like;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    // equals et hashCode — utiles pour la comparaison et les collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // même objet
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return id_like == like.id_like &&
                id_Blog == like.id_Blog &&
                id == like.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_like, id_Blog, id);
    }
}
