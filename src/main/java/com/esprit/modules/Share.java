package com.esprit.modules;

import java.sql.Timestamp;

public class Share {
    private int id_share;
    private int id;         // ID du blog
    private int id_client;
    private Timestamp shareDate;

    // Constructeurs
    public Share() {}

    public Share(int id, int id_client, Timestamp shareDate) {
        this.id = id;
        this.id_client = id_client;
        this.shareDate = shareDate;
    }

    // Getters & Setters
    public int getId_share() { return id_share; }
    public void setId_share(int id_share) { this.id_share = id_share; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_client() { return id_client; }
    public void setId_client(int id_client) { this.id_client = id_client; }

    public Timestamp getShareDate() { return shareDate; }
    public void setShareDate(Timestamp shareDate) { this.shareDate = shareDate; }

    @Override
    public String toString() {
        return String.format("Share{id_share=%d, id_blog=%d, id_client=%d, shareDate=%s}",
                id_share, id, id_client, shareDate);
    }
}