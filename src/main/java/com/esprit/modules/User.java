package com.esprit.modules;

import java.util.Objects;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String nom;
    private String prenom;
    private int numero;
    private int contactUrgence;
    private String role;

    // Constructeur vide
    public User() {
    }

    // Constructeur complet
    public User(int id, String username, String password, String email, String nom, String prenom,
                int numero, int contactUrgence, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.numero = numero;
        this.contactUrgence = contactUrgence;
        this.role = role;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getContactUrgence() {
        return contactUrgence;
    }

    public void setContactUrgence(int contactUrgence) {
        this.contactUrgence = contactUrgence;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // equals() pour comparer par ID et email
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return id == that.id && Objects.equals(email, that.email);
    }

    // hashCode() basé sur ID et email
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // toString() pour affichage utile en debug ou logs
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", numero=" + numero +
                ", contactUrgence=" + contactUrgence +
                ", role='" + role + '\'' +
                '}';
    }
}
