package com.esprit.modules.utilisateurs;

public class Admin extends User {
    public Admin(int id, String username, String password, String email, Role role) {
        super(id, username, password, email, role);
    }

    @Override
    public void afficherDetails() {
        System.out.println("Administrateur: " + getUsername() + " (" + getEmail() + ")");
    }
}