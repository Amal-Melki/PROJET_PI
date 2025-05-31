package com.esprit.modules.produits;


import java.sql.Date;

public class CommandeProduit {
    private int id;
    private int produitId;
    private int utilisateurId;
    private Date dateCommande;
    private int quantite;
    private double prixTotal;
    private String statut;


    public CommandeProduit() {}

    public CommandeProduit(int id, int produitId, int utilisateurId, Date dateCommande, int quantite, double prixTotal, String statut) {
        this.id = id;
        this.produitId = produitId;
        this.utilisateurId = utilisateurId;
        this.dateCommande = dateCommande;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }

    public CommandeProduit(int produitId, int utilisateurId, Date dateCommande, int quantite, double prixTotal, String statut) {
        this.produitId = produitId;
        this.utilisateurId = utilisateurId;
        this.dateCommande = dateCommande;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProduitId() { return produitId; }
    public void setProduitId(int produitId) { this.produitId = produitId; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public Date getDateCommande() { return dateCommande; }
    public void setDateCommande(Date dateCommande) { this.dateCommande = dateCommande; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "CommandeProduit{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", utilisateurId=" + utilisateurId +
                ", dateCommande=" + dateCommande +
                ", quantite=" + quantite +
                ", prixTotal=" + prixTotal +
                ", statut='" + statut + '\'' +
                '}';
    }
}