package com.esprit.models.models;

public class Client extends User {
    private int id_client;
    private int numero_tel;
    private String image_path;

    public Client(int id_client, int numero_tel, String image_path, int id_user, String nom_suser, String prenom_user, String email_user, String password_user) {
        super(id_user, nom_suser, prenom_user, email_user, password_user);
        this.id_client = id_client;
        this.numero_tel = numero_tel;
        this.image_path = image_path;
    }

    public Client(int numero_tel, String image_path, String nom_suser, String prenom_user, String email_user, String password_user) {
        super(nom_suser, prenom_user, email_user, password_user);
        this.numero_tel = numero_tel;
        this.image_path = image_path;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public int getNumero_tel() {
        return numero_tel;
    }

    public void setNumero_tel(int numero_tel) {
        this.numero_tel = numero_tel;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id_client=" + id_client +
                ", numero_tel=" + numero_tel +
                ", image_path='" + image_path + '\'' +
                "} " + super.toString();
    }
} 