package com.esprit.modules;

import java.sql.Date;

public class ReservationMateriel {
    private int id;
    private int materielId;
    private Date dateDebut;
    private Date dateFin;
    private int quantiteReservee;
    private String statut;

    // Constructeurs
    public ReservationMateriel() {}

    public ReservationMateriel(int id, int materielId, Date dateDebut, Date dateFin, int quantiteReservee, String statut) {
        this.id = id;
        this.materielId = materielId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.quantiteReservee = quantiteReservee;
        this.statut = statut;
    }

    public ReservationMateriel(int materielId, Date dateDebut, Date dateFin, int quantiteReservee, String statut) {
        this.materielId = materielId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.quantiteReservee = quantiteReservee;
        this.statut = statut;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMaterielId() { return materielId; }
    public void setMaterielId(int materielId) { this.materielId = materielId; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public int getQuantiteReservee() { return quantiteReservee; }
    public void setQuantiteReservee(int quantiteReservee) { this.quantiteReservee = quantiteReservee; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "ReservationMateriel{" +
                "id=" + id +
                ", materielId=" + materielId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", quantiteReservee=" + quantiteReservee +
                ", statut='" + statut + '\'' +
                '}';
    }
}
