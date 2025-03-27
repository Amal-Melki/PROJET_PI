package com.esprit.modules.utilisateurs;

public class Organisateur extends User {
    private String nomOrganisation;

    public Organisateur(int id, String username, String password, String email, Role role, String nomOrganisation) {
        super(id, username, password, email, role);
        this.nomOrganisation = nomOrganisation;
    }

    public String getNomOrganisation() { return nomOrganisation; }
    public void setNomOrganisation(String nomOrganisation) { this.nomOrganisation = nomOrganisation; }

    @Override
    public void afficherDetails() {
        System.out.println("Organisateur: " + getUsername() + " - Organisation: " + nomOrganisation);
    }

    @Override
    public String toString() {
        return super.toString() + ", nomOrganisation='" + nomOrganisation + "'}";
    }
}