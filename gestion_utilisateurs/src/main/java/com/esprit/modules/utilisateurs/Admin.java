package com.esprit.modules.utilisateurs;

public class Admin extends User {
    // Constructeur
    public Admin(int id, String username, String password, String email, Role role) {
        super(id, username, password, email, role);
    }

    @Override
    public String toString() {
        return "Admin{" + super.toString() + "}";
    }
}