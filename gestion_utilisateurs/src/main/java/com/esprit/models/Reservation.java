package com.esprit.models;

import java.time.LocalDate;

public class Reservation {
    private int id_res;
    private String date_res;
    private int id_user;
    private int id_ev;
    private String status;

    public Reservation() {
    }

    public Reservation(int id_res, String date_res, int id_user, int id_ev, String status) {
        this.id_res = id_res;
        this.date_res = date_res;
        this.id_user = id_user;
        this.id_ev = id_ev;
        this.status = status;
    }

    public Reservation(String date_res, int id_user, int id_ev, String status) {
        this.date_res = date_res;
        this.id_user = id_user;
        this.id_ev = id_ev;
        this.status = status;
    }

    public int getId_res() {
        return id_res;
    }

    public void setId_res(int id_res) {
        this.id_res = id_res;
    }

    public String getDate_res() {
        return date_res;
    }

    public void setDate_res(String date_res) {
        this.date_res = date_res;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_ev() {
        return id_ev;
    }

    public void setId_ev(int id_ev) {
        this.id_ev = id_ev;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id_res=" + id_res +
                ", date_res='" + date_res + '\'' +
                ", id_user=" + id_user +
                ", id_ev=" + id_ev +
                ", status='" + status + '\'' +
                '}';
    }
} 