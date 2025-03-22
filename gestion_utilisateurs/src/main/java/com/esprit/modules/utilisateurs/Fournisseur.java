package com.esprit.modules.utilisateurs;

public class Fournisseur extends User {
    // Attribut spécifique au fournisseur
    private String nomEntreprise;

    // Constructeur
    public Fournisseur(int id, String username, String password, String email, Role role, String nomEntreprise) {
        super(id, username, password, email, role);
        this.nomEntreprise = nomEntreprise;
    }

    // Getter et setter pour le nom de l'entreprise
    public String getNomEntreprise() { return nomEntreprise; }
    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }

    @Override
    public String toString() {
        return "Fournisseur{" + super.toString() + ", nomEntreprise='" + nomEntreprise + "'}";
    }
}