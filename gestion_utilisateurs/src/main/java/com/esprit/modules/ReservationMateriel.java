package com.esprit.modules;

import java.sql.Date;
import java.time.LocalDate;

public class ReservationMateriel {

    private String nomMateriel;
    private int id;
    private int materielId;
    private Date dateDebut;
    private Date dateFin;
    private int quantiteReservee;
    private String statut;
    private double montantTotal;
    private int idClient;

    // Constructeurs
    public ReservationMateriel() {}

    public ReservationMateriel(int id, int materielId, LocalDate dateDebut, LocalDate dateFin, int quantiteReservee, String statut, double montantTotal, int idClient) {
        this.id = id;
        this.materielId = materielId;
        this.dateDebut = Date.valueOf(dateDebut);
        this.dateFin = Date.valueOf(dateFin);
        this.quantiteReservee = quantiteReservee;
        this.statut = statut;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
    }

    public ReservationMateriel(int id, int materielId, LocalDate dateDebut, LocalDate dateFin, int quantiteReservee, String statut) {
        this(id, materielId, dateDebut, dateFin, quantiteReservee, statut, 0.0, 0);
    }

    // Getters et Setters
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

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
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
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", quantiteReservee=" + quantiteReservee +
                ", statut='" + statut + '\'' +
                ", montantTotal=" + montantTotal +
                ", idClient=" + idClient +
                '}';
    }
}
