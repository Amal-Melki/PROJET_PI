package com.esprit.models.models;

import java.time.LocalDate;

public class Evenement {
    private int id_ev;
    private String title;
    private String description_ev;
    private LocalDate date_debut;
    private LocalDate date_fin;
    private String latitude;
    private String longitude;
    private String categories;
    private int nbr_places_dispo;

    public Evenement() {
    }

    public Evenement(int id_ev, String title, String description_ev, LocalDate date_debut, LocalDate date_fin, 
                    String latitude, String longitude, String categories, int nbr_places_dispo) {
        this.id_ev = id_ev;
        this.title = title;
        this.description_ev = description_ev;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categories = categories;
        this.nbr_places_dispo = nbr_places_dispo;
    }

    public Evenement(String title, String description_ev, LocalDate date_debut, LocalDate date_fin, 
                    String latitude, String longitude, String categories, int nbr_places_dispo) {
        this.title = title;
        this.description_ev = description_ev;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categories = categories;
        this.nbr_places_dispo = nbr_places_dispo;
    }

    public int getId_ev() {
        return id_ev;
    }

    public void setId_ev(int id_ev) {
        this.id_ev = id_ev;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription_ev() {
        return description_ev;
    }

    public void setDescription_ev(String description_ev) {
        this.description_ev = description_ev;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getNbr_places_dispo() {
        return nbr_places_dispo;
    }

    public void setNbr_places_dispo(int nbr_places_dispo) {
        this.nbr_places_dispo = nbr_places_dispo;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id_ev=" + id_ev +
                ", title='" + title + '\'' +
                ", description_ev='" + description_ev + '\'' +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", categories='" + categories + '\'' +
                ", nbr_places_dispo=" + nbr_places_dispo +
                '}';
    }
} 