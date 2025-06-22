package com.esprit.models;

import java.sql.Timestamp;
import java.util.Objects;

public class Like {
    private int id_like;
    private int id_Blog;
    private Integer id_user;  // Changé en Integer pour permettre null
    private Integer id_client; // Changé en Integer pour permettre null
    private Timestamp date_like;
    private User utilisateur;

    // Constructeurs
    public Like() {}

    // Constructeur pour user
    public Like(int id_like, int id_Blog, int id_user, Timestamp date_like) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id_user = id_user;
        this.id_client = null;
        this.date_like = date_like;
    }

    // Constructeur pour client
    public Like(int id_like, int id_Blog, Timestamp date_like, int id_client) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id_user = null;
        this.id_client = id_client;
        this.date_like = date_like;
    }

    // Constructeur complet
    public Like(int id_like, int id_Blog, Integer id_user, Integer id_client,
                Timestamp date_like, User utilisateur) {
        this.id_like = id_like;
        this.id_Blog = id_Blog;
        this.id_user = id_user;
        this.id_client = id_client;
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

    public Integer getId_user() {
        return id_user;
    }

    public void setId_user(Integer id_user) {
        this.id_user = id_user;
    }

    public Integer getId_client() {
        return id_client;
    }

    public void setId_client(Integer id_client) {
        this.id_client = id_client;
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

    // Méthode utilitaire pour déterminer le type
    public boolean isClientLike() {
        return id_client != null;
    }

    // equals et hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return id_like == like.id_like &&
                id_Blog == like.id_Blog &&
                Objects.equals(id_user, like.id_user) &&
                Objects.equals(id_client, like.id_client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_like, id_Blog, id_user, id_client);
    }

    @Override
    public String toString() {
        return "Like{" +
                "id_like=" + id_like +
                ", id_Blog=" + id_Blog +
                ", id_user=" + id_user +
                ", id_client=" + id_client +
                ", date_like=" + date_like +
                '}';
    }
}