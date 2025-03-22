package com.esprit.modules.utilisateurs;

public class Moderateur extends User {
    // Constructeur
    public Moderateur(int id, String username, String password, String email, Role role) {
        super(id, username, password, email, role);
    }

    @Override
    public String toString() {
        return "Moderateur{" + super.toString() + "}";
    }
}