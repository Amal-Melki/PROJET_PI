package com.esprit.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationEspace {
    private int reservationId; // Identifiant de la réservation
    private Espace espace;     // L'espace réservé
    private Date dateDebut;    // Date de début de la réservation
    private Date dateFin;      // Date de fin de la réservation
    private String utilisateur; // Utilisateur ayant effectué la réservation
    private List<Equipement> equipements; // Liste des équipements utilisés

    // Constructeur avec ID
    public ReservationEspace(int reservationId, Espace espace, Date dateDebut, Date dateFin, String utilisateur, List<Equipement> equipements) {
        this.reservationId = reservationId;
        this.espace = espace;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateur = utilisateur;
        this.equipements = (equipements != null) ? equipements : new ArrayList<>();
    }

    // Constructeur sans ID
    public ReservationEspace(Espace espace, Date dateDebut, Date dateFin, String utilisateur, List<Equipement> equipements) {
        this.espace = espace;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateur = utilisateur;
        this.equipements = (equipements != null) ? equipements : new ArrayList<>();
    }

    // Getters et Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int id) {
        this.reservationId = id;
    }

    public Espace getEspace() {
        return espace;
    }

    public void setEspace(Espace espace) {
        this.espace = espace;
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

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Equipement> getEquipements() {
        return equipements;
    }

    public void setEquipements(List<Equipement> equipements) {
        this.equipements = (equipements != null) ? equipements : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ReservationEspace{" +
                "reservationId=" + reservationId +
                ", espace=" + espace +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", utilisateur='" + utilisateur + '\'' +
                ", equipements=" + equipements +
                '}';
    }
}
