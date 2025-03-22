package com.esprit.modules.utilisateurs;

public class Organisateur extends User {
    // Attribut spécifique à l'organisateur
    private String nomOrganisation;

    // Constructeur
    public Organisateur(int id, String username, String password, String email, Role role, String nomOrganisation) {
        super(id, username, password, email, role);
        this.nomOrganisation = nomOrganisation;
    }

    // Getter et setter pour le nom de l'organisation
    public String getNomOrganisation() { return nomOrganisation; }
    public void setNomOrganisation(String nomOrganisation) { this.nomOrganisation = nomOrganisation; }

    @Override
    public String toString() {
        return "Organisateur{" + super.toString() + ", nomOrganisation='" + nomOrganisation + "'}";
    }
}