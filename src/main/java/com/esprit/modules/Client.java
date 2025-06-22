package com.esprit.modules;

public class Client {
    private int id;
    private int numeroTel;
    private int idUser;
    private String imagePath;

    // Constructeurs
    public Client() {}

    public Client(int id, int numeroTel, int idUser, String imagePath) {
        this.id = id;
        this.numeroTel = numeroTel;
        this.idUser = idUser;
        this.imagePath = imagePath;
    }

    public Client(int numeroTel, int idUser, String imagePath) {
        this.numeroTel = numeroTel;
        this.idUser = idUser;
        this.imagePath = imagePath;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(int numeroTel) {
        this.numeroTel = numeroTel;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Client #" + id + " - Tél: " + numeroTel;
    }
}
