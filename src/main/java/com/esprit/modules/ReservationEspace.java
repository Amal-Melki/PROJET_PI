package com.esprit.modules;
import java.time.LocalDate;


public class ReservationEspace {
    private int reservationId;
    private int espaceId;
    private String user;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public ReservationEspace(int reservationId, int espaceId, String user, LocalDate dateDebut, LocalDate dateFin) {
        this.reservationId = reservationId;
        this.espaceId = espaceId;
        this.user = user;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public ReservationEspace(int espaceId, String user, LocalDate dateDebut, LocalDate dateFin) {
        this.espaceId = espaceId;
        this.user = user;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getEspaceId() {
        return espaceId;
    }

    public void setEspaceId(int espaceId) {
        this.espaceId = espaceId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    @Override
    public String toString() {
        return "ReservationEspace{" +
                "reservationId=" + reservationId +
                ", espaceId=" + espaceId +
                ", user='" + user + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                '}';
    }

    public void initData(Espace espaceSelectionne) {

    }
}