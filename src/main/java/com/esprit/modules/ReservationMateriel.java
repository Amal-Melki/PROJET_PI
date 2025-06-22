package com.esprit.modules;

import java.time.LocalDate; // Importer java.time.LocalDate

public class ReservationMateriel {

    private String nomMateriel; // Nom du matériel (utile pour l'affichage)
    private int id;
    private int materielId;
    private LocalDate dateDebut; // <-- Changer le type en LocalDate
    private LocalDate dateFin;   // <-- Changer le type en LocalDate
    private int quantiteReservee;
    private String statut;
    private double montantTotal;
    private int idClient;

    // Constructeurs
    public ReservationMateriel() {}

    // Constructeur complet qui prend des LocalDate pour les dates
    public ReservationMateriel(int id, int materielId, LocalDate dateDebut, LocalDate dateFin, int quantiteReservee, String statut, double montantTotal, int idClient) {
        this.id = id;
        this.materielId = materielId;
        this.dateDebut = dateDebut; // Ne pas convertir ici, on reçoit déjà un LocalDate
        this.dateFin = dateFin;     // Ne pas convertir ici, on reçoit déjà un LocalDate
        this.quantiteReservee = quantiteReservee;
        this.statut = statut;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
    }

    // Constructeur simplifié (délègue au constructeur complet)
    public ReservationMateriel(int id, int materielId, LocalDate dateDebut, LocalDate dateFin, int quantiteReservee, String statut) {
        // Appelle le constructeur complet avec des valeurs par défaut pour montantTotal et idClient
        this(id, materielId, dateDebut, dateFin, quantiteReservee, statut, 0.0, 0);
    }


    // Getters et Setters - TRÈS IMPORTANT : Ils doivent maintenant utiliser LocalDate
    public String getNomMateriel() {
        return nomMateriel;
    }

    public void setNomMateriel(String nomMateriel) {
        this.nomMateriel = nomMateriel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaterielId() {
        return materielId;
    }

    public void setMaterielId(int materielId) {
        this.materielId = materielId;
    }

    public LocalDate getDateDebut() { // <-- Changer le type de retour en LocalDate
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) { // <-- Changer le type de paramètre en LocalDate
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() { // <-- Changer le type de retour en LocalDate
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) { // <-- Changer le type de paramètre en LocalDate
        this.dateFin = dateFin;
    }

    public int getQuantiteReservee() {
        return quantiteReservee;
    }

    public void setQuantiteReservee(int quantiteReservee) {
        this.quantiteReservee = quantiteReservee;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return "ReservationMateriel{" +
                "id=" + id +
                ", materielId=" + materielId +
                ", dateDebut=" + dateDebut + // LocalDate s'affichera bien
                ", dateFin=" + dateFin +     // LocalDate s'affichera bien
                ", quantiteReservee=" + quantiteReservee +
                ", statut='" + statut + '\'' +
                ", montantTotal=" + montantTotal +
                ", idClient=" + idClient +
                '}';
    }
}