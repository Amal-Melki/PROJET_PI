package com.esprit.modules.utilisateurs;

public class Fournisseur extends User {
    private String nomEntreprise;

    public Fournisseur(int id, String username, String password, String email, Role role, String nomEntreprise) {
        super(id, username, password, email, role);
        this.nomEntreprise = nomEntreprise;
    }

    public String getNomEntreprise() { return nomEntreprise; }
    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }

    @Override
    public void afficherDetails() {
        System.out.println("Fournisseur: " + getUsername() + " - Entreprise: " + nomEntreprise);
    }

    @Override
    public String toString() {
        return super.toString() + ", nomEntreprise='" + nomEntreprise + "'}";
    }
}