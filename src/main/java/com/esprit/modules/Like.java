package com.esprit.modules;

import java.sql.Timestamp;
import java.util.Objects;

public class Like {
    private int id_like;
    private int id_Blog;
    private int id_user;  // Renommé pour correspondre à la table 'user'
    private Timestamp date_like;
    private User utilisateur;

    // Constructeur vide
    public Like() {
    }

    // Constructeur sans utilisateur
    public Like(int id_like, int id_Blog, int id_user, Timestamp date_like) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id_user = id_user;
        this.date_like = date_like;
    }

    // Constructeur complet
    public Like(int id_like, int id_Blog, int id_user, Timestamp date_like, User utilisateur) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id_user = id_user;
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

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
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

    // equals et hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return id_like == like.id_like &&
                id_Blog == like.id_Blog &&
                id_user == like.id_user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_like, id_Blog, id_user);
    }
}