package com.esprit.models;

public class Admin extends User {
    private int id_admin;
    private int role;

    public Admin(int id_admin, int role, int id_user, String nom_suser, String prenom_user, String email_user, String password_user) {
        super(id_user, nom_suser, prenom_user, email_user, password_user);
        this.id_admin = id_admin;
        this.role = role;
    }

    public Admin(int role, String nom_suser, String prenom_user, String email_user, String password_user) {
        super(nom_suser, prenom_user, email_user, password_user);
        this.role = role;
    }

    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id_admin) {
        this.id_admin = id_admin;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id_admin=" + id_admin +
                ", role=" + role +
                "} " + super.toString();
    }
} 