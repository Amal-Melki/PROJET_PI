package com.esprit.modules;

import javafx.beans.property.*;

public class Client {
    private final IntegerProperty id_client = new SimpleIntegerProperty();
    private final IntegerProperty numero_tel = new SimpleIntegerProperty();
    private final IntegerProperty id_user = new SimpleIntegerProperty();
    private final StringProperty image_path = new SimpleStringProperty();

    public Client() {}

    public Client(int id_client, int numero_tel, int id_user, String image_path) {
        this.id_client.set(id_client);
        this.numero_tel.set(numero_tel);
        this.id_user.set(id_user);
        this.image_path.set(image_path);
    }

    // Property getters
    public IntegerProperty id_clientProperty() { return id_client; }
    public IntegerProperty numero_telProperty() { return numero_tel; }
    public IntegerProperty id_userProperty() { return id_user; }
    public StringProperty image_pathProperty() { return image_path; }

    // Standard getters
    public int getId_client() { return id_client.get(); }
    public int getNumero_tel() { return numero_tel.get(); }
    public int getId_user() { return id_user.get(); }
    public String getImage_path() { return image_path.get(); }

    // Setters
    public void setId_client(int id) { this.id_client.set(id); }
    public void setNumero_tel(int tel) { this.numero_tel.set(tel); }
    public void setId_user(int id) { this.id_user.set(id); }
    public void setImage_path(String path) { this.image_path.set(path); }

    @Override
    public String toString() {
        return "Client{" +
                "id_client=" + getId_client() +
                ", numero_tel=" + getNumero_tel() +
                ", id_user=" + getId_user() +
                ", image_path='" + getImage_path() + '\'' +
                '}';
    }
}