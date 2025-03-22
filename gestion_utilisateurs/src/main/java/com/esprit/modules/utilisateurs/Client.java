package com.esprit.modules.utilisateurs;

public class Client extends User {
    // Attribut spécifique au client
    private String adresse;

    // Constructeur
    public Client(int id, String username, String password, String email, Role role, String adresse) {
        super(id, username, password, email, role);
        this.adresse = adresse;
    }

    // Getter et setter pour l'adresse
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public String toString() {
        return "Client{" + super.toString() + ", adresse='" + adresse + "'}";
    }
}