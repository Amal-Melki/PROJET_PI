package com.esprit.modules.produits;

import java.sql.Timestamp;

public class Utilisateur {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private Timestamp dateInscription;

    // Constructeur par défaut
    public Utilisateur() {
    }

    // Constructeur avec paramètres
    public Utilisateur(int id, String username, String password, String email, String role, Timestamp dateInscription) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.dateInscription = dateInscription;
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
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("L'email n'est pas valide");
        }
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || !(role.equals("admin") || role.equals("user"))) {
            throw new IllegalArgumentException("Le rôle doit être 'admin' ou 'user'");
        }
        this.role = role;
    }

    public Timestamp getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Timestamp dateInscription) {
        if (dateInscription == null) {
            throw new IllegalArgumentException("La date d'inscription ne peut pas être null");
        }
        this.dateInscription = dateInscription;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
}