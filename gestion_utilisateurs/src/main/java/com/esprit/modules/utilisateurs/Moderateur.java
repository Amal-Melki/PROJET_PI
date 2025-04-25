package com.esprit.modules.utilisateurs;

public class Moderateur extends User {
    public Moderateur(int id, String username, String password, String email, Role role) {
        super(id, username, password, email, role);
    }

    @Override
    public void afficherDetails() {
        System.out.println("Modérateur: " + getUsername() + " - Email: " + getEmail());
    }
}