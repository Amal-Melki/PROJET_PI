package com.esprit.models.models;

public class User {
    private int id_user;
    private String nom_suser;
    private String prenom_user;
    private String email_user;
    private String password_user;

    public User(int id_user, String nom_suser, String prenom_user, String email_user, String password_user) {
        this.id_user = id_user;
        this.nom_suser = nom_suser;
        this.prenom_user = prenom_user;
        this.email_user = email_user;
        this.password_user = password_user;
    }

    public User(String nom_suser, String prenom_user, String email_user, String password_user) {
        this.nom_suser = nom_suser;
        this.prenom_user = prenom_user;
        this.email_user = email_user;
        this.password_user = password_user;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNom_suser() {
        return nom_suser;
    }

    public void setNom_suser(String nom_suser) {
        this.nom_suser = nom_suser;
    }

    public String getPrenom_user() {
        return prenom_user;
    }

    public void setPrenom_user(String prenom_user) {
        this.prenom_user = prenom_user;
    }

    public String getEmail_user() {
        return email_user;
    }

    public void setEmail_user(String email_user) {
        this.email_user = email_user;
    }

    public String getPassword_user() {
        return password_user;
    }

    public void setPassword_user(String password_user) {
        this.password_user = password_user;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", nom_suser='" + nom_suser + '\'' +
                ", prenom_user='" + prenom_user + '\'' +
                ", email_user='" + email_user + '\'' +
                ", password_user='" + password_user + '\'' +
                '}';
    }
} 