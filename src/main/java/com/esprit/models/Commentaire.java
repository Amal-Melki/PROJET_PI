package com.esprit.models;


import java.sql.Timestamp;
import java.util.Objects;


public class Commentaire {
    private int id_Commentaire;
    private int id_user;
    private int id;
    private String description;
    private Timestamp date_Commentaire;
    private User utilisateur;


    public Commentaire() {

    }

    public Commentaire(int id_Commentaire, int id, int id_user, String description, Timestamp date_Commentaire) {
        this.id_Commentaire = id_Commentaire;
        this.id = id;
        this.id_user = id_user;
        this.description = description;
        this.date_Commentaire = date_Commentaire;
    }

    public Commentaire(int id_Commentaire, int id, int id_user, String description, Timestamp date_Commentaire, User utilisateur) {
        this.id_Commentaire = id_Commentaire;
        this.id = id;
        this.id_user = id_user;
        this.description = description;
        this.date_Commentaire = date_Commentaire;
        this.utilisateur = utilisateur;
    }

    public Timestamp getDate_Commentaire() {
        return date_Commentaire;
    }

    public void setDate_Commentaire(Timestamp date_Commentaire) {
        this.date_Commentaire = date_Commentaire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_Commentaire() {
        return id_Commentaire;
    }

    public void setId_Commentaire(int id_Commentaire) {
        this.id_Commentaire = id_Commentaire;
    }

    public int getId_Blog() {
        return id_user;
    }

    public void setId_Blog(int id_Blog) {
        this.id_user = id_user;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id_Commentaire=" + id_Commentaire +
                ", id=" + id +
                ", id_Blog=" + id_user +
                ", description='" + description + '\'' +
                ", date_Commentaire=" + date_Commentaire +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Commentaire that = (Commentaire) o;
        return id_Commentaire == that.id_Commentaire && id == that.id && id_user == that.id_user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_Commentaire, id, id_user);
    }
}
