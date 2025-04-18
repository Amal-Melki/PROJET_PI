package com.esprit.modules;

import java.sql.Date;
import java.time.LocalDate;

public class ReservationMateriel {

    private String nomMateriel;

    public String getNomMateriel() {
        return nomMateriel;
    }

    public void setNomMateriel(String nomMateriel) {
        this.nomMateriel = nomMateriel;
    }

    private int id;
    private int materielId;
    private Date dateDebut;
    private Date dateFin;
    private int quantiteReservee;
    private String statut;

    // Constructeurs
    public ReservationMateriel() {}

    // ✅ Ce constructeur permet d'utiliser LocalDate directement depuis le contrôleur
    public ReservationMateriel(int id, int materielId, LocalDate dateDebut, LocalDate dateFin, int quantiteReservee, String statut) {
        this.id = id;
        this.materielId = materielId;
        this.dateDebut = Date.valueOf(dateDebut); // conversion
        this.dateFin = Date.valueOf(dateFin);     // conversion
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
