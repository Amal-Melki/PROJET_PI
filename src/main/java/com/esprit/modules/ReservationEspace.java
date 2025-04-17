package com.esprit.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationEspace {
    private int reservationId;
    private Espace espace;
    private Date dateDebut;
    private Date dateFin;
    private int userId;
    private List<Materiel> materiels;

    // Constructeur avec ID
    public ReservationEspace(int reservationId, Espace espace, Date dateDebut, Date dateFin, int userId, List<Materiel> equipements) {
        this.reservationId = reservationId;
        this.espace = espace;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.userId = userId;
        this.materiels = (materiels != null) ? materiels : new ArrayList<>();
    }

    // Constructeur sans ID
    public ReservationEspace(Espace espace, Date dateDebut, Date dateFin, int userId, List<Materiel> materiels) {
        this.espace = espace;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.userId = userId;
        this.materiels = (materiels != null) ? materiels : new ArrayList<>();
    }

    // Getters et Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Materiel> getEquipements() {
        return materiels;
    }

    public void setEquipements(List<Materiel> materiels) {
        this.materiels = (materiels != null) ? materiels : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ReservationEspace{" +
                "reservationId=" + reservationId +
                ", espace=" + espace +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", userId=" + userId +
                ", materiels=" + materiels +
                '}';
    }
}
