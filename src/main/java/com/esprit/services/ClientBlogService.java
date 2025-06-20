package com.esprit.services;

import com.esprit.modules.Client;
import java.util.ArrayList;
import java.util.List;

public class ClientBlogService {
    private final List<Client> clients = new ArrayList<>();
    private int lastId = 0;

    public void ajouterClient(Client client) {
        client.setId_client(++lastId);
        clients.add(client);
        System.out.println("Client ajouté: " + client);
    }

    public List<Client> recupererClients() {
        return new ArrayList<>(clients);
    }

    public void partagerClient(int idClient) {
        System.out.println("Client partagé ID: " + idClient);
    }

    public void ajouterCommentaire(int idClient, String commentaire) {
        System.out.println("Commentaire ajouté pour client ID " + idClient + ": " + commentaire);
    }

    public void ajouterLike(int idClient) {
        System.out.println("Like ajouté pour client ID: " + idClient);
    }
}