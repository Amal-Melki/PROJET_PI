package com.esprit.modules.utilisateurs;

public class Client extends User {
    private String adresse;

    public Client(int id, String username, String password, String email, Role role, String adresse) {
        super(id, username, password, email, role);
        this.adresse = adresse;
    }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public void afficherDetails() {
        System.out.println("Client: " + getUsername() + " - Adresse: " + adresse);
    }

    @Override
    public String toString() {
        return super.toString() + ", adresse='" + adresse + "'}";
    }
}